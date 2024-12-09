package com.example.p2pchat.serverclient.Client;

import com.example.p2pchat.model.ClientData;
import com.example.p2pchat.model.ServerData;
import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ClientConnectionThread extends Thread {

    private final String username;
    private final String uuid;

    ServerSocket listenerServerSocket;
    List<ClientListenerI> clientListeners;

    Hashtable<SocketAddress, ClientChatThread> chats2;

    public ClientConnectionThread(ServerSocket serverSocket, String username, String uuid) {
        this.listenerServerSocket = serverSocket;
        this.username = username;
        this.uuid = uuid;
        this.clientListeners = new ArrayList<>();

        this.chats2 = new Hashtable<>();
    }

    public void close() throws IOException {
        listenerServerSocket.close();
        for (ClientChatThread chat : chats2.values()) {
            chat.close();
        }
    }

    public void run() {
        System.out.println("The ConnectionThread is listening on port " + listenerServerSocket.getLocalPort());
        try {
            while (true) {
                processInviteSocket();
                System.out.println("New chat request");
            }
        } catch (Exception e) {
            System.out.println("ConnectionListenerThread Error: " + e.getMessage());
        }
    }

    public void processInviteSocket() throws IOException, ClassNotFoundException {
        // Accept socket and add it in pendingSockets.
        Socket socket = listenerServerSocket.accept();

        ObjectInputStream inputObject = new ObjectInputStream(socket.getInputStream());

        ServerData data = (ServerData) inputObject.readObject();
        if(chats2.containsKey(data.socketAddress())) {
            socket.close();
            return;
        }
        addChat(data.socketAddress(), new ClientData(data.id(), data.name(), socket));
        updateUI();
    }

    public void updateUI() {
        // Notify GUI about connections update.
        Platform.runLater(() -> clientListeners.forEach(ClientListenerI::onConnection));
    }

    public void acceptPendingConnection(SocketAddress address) {
        try {
            ClientChatThread chatThread = chats2.get(address);
            chatThread.isPending = false;
            chatThread.sendMessage("Chat Request Accepted!"); //TODO this looks not good.
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            updateUI();
            //DEBUG
            System.out.println("Chat Request Accepted");
        }
    }

    public void rejectPendingConnection(SocketAddress address) {

        try {
            chats2.get(address).close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            chats2.remove(address);
            updateUI();
            //DEBUG
            System.out.println("Chat Request Rejected");
        }
    }

    public void requestChatConnect(SocketAddress address) {
        //Check that we don't have pending/approved connections with this user. If it does, abort the connection.
        if(chats2.containsKey(address)) {
            return;
        }
        try {
            Socket socket = new Socket();
            socket.connect(address);

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());

            objectOutput.writeObject(new ServerData(uuid, username, listenerServerSocket.getLocalSocketAddress()));

            ClientData clientData = new ClientData(uuid, username, socket);

            ClientChatThread chatThread = addChat(address, clientData);
            chatThread.isPending = false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addPendingConnectionListener(ClientListenerI listener) {
        clientListeners.add(listener);
    }

    private ClientChatThread addChat(SocketAddress address, ClientData data) {
        // Create ClientChatThread and add it to the chats pool
        try {
            ClientChatThread thread = new ClientChatThread(data, address, chats2, clientListeners);
            thread.start();
            chats2.put(address, thread);
            return thread;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientChatThread getChat(SocketAddress socketAddress) {
        return chats2.get(socketAddress);
    }

    public boolean chatsContains(SocketAddress socketAddress) {
        return chats2.containsKey(socketAddress);
    }

    public void closeChat(SocketAddress socketAddress) {
        try {
            chats2.get(socketAddress).close();
        } catch (IOException e) {
            //ignore
        }
    }
}
