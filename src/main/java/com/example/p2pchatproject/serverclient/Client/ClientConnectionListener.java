package com.example.p2pchatproject.serverclient.Client;

import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ClientConnectionListener extends Thread {

    ServerSocket listenerServerSocket;
    List<ClientChatThread> chats;
    Hashtable<SocketAddress, Socket> pendingSockets;
    List<PendingConnectionListener> pendingConnectionListeners;

    public ClientConnectionListener(ServerSocket serverSocket){
        this.listenerServerSocket = serverSocket;
        this.pendingSockets = new Hashtable<>();
        this.pendingConnectionListeners = new ArrayList<>();
        this.chats = new ArrayList<>();
    }

    public void close() throws IOException {
        listenerServerSocket.close();
    }

    public void run(){
        System.out.println("The ConnectionListener is listening on port " + listenerServerSocket.getLocalPort());
        try {
            while (true) {
                acceptInviteSocket();

                boolean acceptChatOrNot = true; // Todo Handle this bozo
//                resolveInvite(address, acceptChatOrNot);
                System.out.println("New chat request");

                // Todo need to make it that invites besides first from the same socket address are ignored.
                //  ( we need only first one anyway )
            }
        } catch (Exception e) {
            System.out.println("CONNECTIONLISTENER ERROR: " + e.getMessage());
        }
    }

    public void acceptInviteSocket() throws IOException, ClassNotFoundException {
        // Accept socket and add it in pendingSockets.
        Socket socket = listenerServerSocket.accept();

        ObjectInputStream inputObject = new ObjectInputStream(socket.getInputStream());
        SocketAddress address = (SocketAddress) inputObject.readObject();
        pendingSockets.put(address, socket);
        updatePendingConnections();
    }

    public void updatePendingConnections() {
        // Notify GUI about connections update.
        Platform.runLater(() -> pendingConnectionListeners.forEach(PendingConnectionListener::onConnection));
    }

    public void acceptPendingConnection(SocketAddress address){
        Socket socket = pendingSockets.get(address);
        try {
            chats.add(new ClientChatThread(socket)); //TODO handle this!
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            pendingSockets.remove(address);
            updatePendingConnections();
            //DEBUG
            System.out.println("Chat Request Accepted");
        }
    }

    public void rejectPendingConnection(SocketAddress address){
        Socket socket = pendingSockets.get(address);
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            pendingSockets.remove(address);
            updatePendingConnections();
            //DEBUG
            System.out.println("Chat Request Rejected");
        }
    }

    public void requestChatConnect(SocketAddress socketAddress) {
        try {
            Socket socket = new Socket();
            socket.connect(socketAddress);

            OutputStream output = socket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);

            // Send listener socket address as identifier.
            objectOutput.writeObject(listenerServerSocket.getLocalSocketAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            chats.add(new ClientChatThread(socket));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPendingConnectionListener(PendingConnectionListener listener) {
        pendingConnectionListeners.add(listener);
    }

    public int getPendingConnections() {
        return pendingSockets.size();
    }
}
