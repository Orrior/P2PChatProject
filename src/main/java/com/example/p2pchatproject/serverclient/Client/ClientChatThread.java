package com.example.p2pchatproject.serverclient.Client;

import com.example.p2pchatproject.model.ClientDataV2;
import com.example.p2pchatproject.model.MessageData;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ClientChatThread extends Thread{
    private final SocketAddress socketAddress;
    private final Socket socket;
    private final Hashtable<SocketAddress, ClientChatThread> chats;

    private final ObjectOutputStream output2;
    private final ObjectInputStream input2;

    private final String username;
    private final String uuid;

    public boolean isPending;
    List<ClientListenerI> clientListeners;
    List<MessageData> chatHistory2;

    public ClientChatThread(ClientDataV2 clientData, SocketAddress socketAddress,
                            Hashtable<SocketAddress, ClientChatThread> chats,
                            List<ClientListenerI> clientListeners) throws IOException {
        isPending = true;

        this.username = clientData.name();
        this.uuid = clientData.id();
        this.socketAddress = socketAddress;
        this.socket = clientData.socket();
        this.chats = chats;
        this.clientListeners = clientListeners;
        chatHistory2 = new ArrayList<>();

        output2 =  new ObjectOutputStream(socket.getOutputStream());
        input2 = new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
        try {
            while(true) {
                MessageData message = (MessageData) input2.readObject();
                if(message == null) {break;}
                chatHistory2.add(message);
                updateUI();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ClientChatThread Error: " + e.getMessage());
        } finally {
            chats.remove(socketAddress);
            disconnect();
        }
    }

    public void sendMessage(String text) {
        try {
            MessageData message = new MessageData(username, uuid, text, LocalDateTime.now());
            output2.writeObject(message);
            chatHistory2.add(message);
        } catch (IOException e) {
            System.out.println("ClientChatThread sendMessage Error: " + e.getMessage());
        }
    }

    public List<MessageData> getChatHistory() {
        return chatHistory2;
    }

    public void close() throws IOException {
        socket.close();
    }

    private void updateUI() {
        // Notify GUI about message update.
        Platform.runLater(() -> clientListeners.forEach(x -> x.onMessage(socketAddress)));
    }

    private void disconnect() {
        Platform.runLater(() -> clientListeners.forEach(ClientListenerI::onDisconnect));
    }
}
