package com.example.p2pchatproject.serverclient.Client;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ClientChatThread extends Thread{
    private final SocketAddress socketAddress;
    private Socket socket;
    private Hashtable<SocketAddress, ClientChatThread> chats;
    List<ClientListenerI> clientListeners;

    private PrintWriter output;
    private BufferedReader input;

    List<String> chatHistory;

    public ClientChatThread(SocketAddress socketAddress, Socket socket, Hashtable<SocketAddress,
            ClientChatThread> chats, List<ClientListenerI> clientListeners) throws IOException {
        this.socketAddress = socketAddress;
        this.socket = socket;
        this.chats = chats;
        this.clientListeners = clientListeners;
        chatHistory = new ArrayList<>();

        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run(){
        try {
            while(true){
                String message = input.readLine();
                if(message == null) {break;}
                chatHistory.add(message);
                updateUI();
            }
        } catch (IOException e) {
            System.out.println("ClientChatThread Error: " + e.getMessage());
        } finally {
            chats.remove(socketAddress);
            disconnect();
        }
    }

    public void sendMessage(String text){
        output.println(text);
        chatHistory.add(text);
    }

    public List<String> getChatHistory(){
        return chatHistory;
    }

    public void close() throws IOException {
        socket.close();
    }

    private void updateUI(){
        // Notify GUI about message update.
        Platform.runLater(() -> clientListeners.forEach(x -> x.onMessage(socketAddress)));
    }

    private void disconnect(){
        Platform.runLater(() -> clientListeners.forEach(x -> x.onDisconnect()));
    }
}
