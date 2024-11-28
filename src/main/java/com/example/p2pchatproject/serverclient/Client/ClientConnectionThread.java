package com.example.p2pchatproject.serverclient.Client;

import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ClientConnectionThread extends Thread {

    ServerSocket listenerServerSocket;
    Hashtable<SocketAddress, ClientChatThread> chats;
    Hashtable<SocketAddress, Socket> pendingSockets;
    List<ClientListenerI> clientListeners;

    public ClientConnectionThread(ServerSocket serverSocket) {
        this.listenerServerSocket = serverSocket;
        this.pendingSockets = new Hashtable<>();
        this.chats = new Hashtable<>();
        this.clientListeners = new ArrayList<>();
    }

    public void close() throws IOException {
        listenerServerSocket.close();
        for (ClientChatThread chat : chats.values()) {
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
        SocketAddress address = (SocketAddress) inputObject.readObject();

        //Check that we don't have pending/approved connections with this user. If it does, abort the connection.
        if(pendingSockets.containsKey(address) || chats.containsKey(address)){
            socket.close();
            return;
        }

        pendingSockets.put(address, socket);
        updateUI();
    }

    public void updateUI() {
        // Notify GUI about connections update.
        Platform.runLater(() -> clientListeners.forEach(ClientListenerI::onConnection));
    }

    public void acceptPendingConnection(SocketAddress address) {
        Socket socket = pendingSockets.get(address);
        try {
            ClientChatThread chatThread = addChat(address, socket);
            chatThread.sendMessage("Chat Request Accepted!"); //TODO this looks not good.
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            pendingSockets.remove(address);
            updateUI();
            //DEBUG
            System.out.println("Chat Request Accepted");
        }
    }

    public void rejectPendingConnection(SocketAddress address) {
        Socket socket = pendingSockets.get(address);
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            pendingSockets.remove(address);
            updateUI();
            //DEBUG
            System.out.println("Chat Request Rejected");
        }
    }

    public void requestChatConnect(SocketAddress socketAddress) {
        //Check that we don't have pending/approved connections with this user. If it does, abort the connection.
        if(pendingSockets.containsKey(socketAddress) || chats.containsKey(socketAddress)){
            return;
        }
        try {
            Socket socket = new Socket();
            socket.connect(socketAddress);

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(listenerServerSocket.getLocalSocketAddress());

            // Send listener socket address as identifier.
            //TODO CHECK IF SUCH IP ALREADY REGISTERED IN CHAT OR PENDING CONNECTIONS.
            addChat(socketAddress, socket);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addPendingConnectionListener(ClientListenerI listener) {
        clientListeners.add(listener);
    }

    public int getPendingConnections() {
        return pendingSockets.size();
    }

    private ClientChatThread addChat(SocketAddress socketAddress, Socket socket) {
        // Create ClientChatThread and add it to the chats pool
        try {
            ClientChatThread thread = new ClientChatThread(socketAddress, socket, chats, clientListeners);
            thread.start();
            chats.put(socketAddress, thread);
            return thread;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientChatThread getChat(SocketAddress socketAddress){
        return chats.get(socketAddress);
    }

    public boolean chatsContains(SocketAddress socketAddress){
        return chats.containsKey(socketAddress);
    }
}
