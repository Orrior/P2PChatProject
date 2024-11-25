package com.example.p2pchatproject.serverclient.Client;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class ClientChatThread extends Thread{
    private final SocketAddress socketAddress;
    private Socket socket;
    private Hashtable<SocketAddress, ClientChatThread> chats;

    private PrintWriter output;
    private BufferedReader input;

    List<String> chatHistory;

    public ClientChatThread(SocketAddress socketAddress, Socket socket, Hashtable<SocketAddress, ClientChatThread> chats){
        this.socketAddress = socketAddress;
        this.socket = socket;
        this.chats = chats;
        chatHistory = new ArrayList<>();
    }

    public void run(){
        // If response {
        // REFRESH UI
        // }
        try {
            output = new PrintWriter(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while(true){
                String message = input.readLine();

                if(message == null) {
                    break;
                }

                chatHistory.add(message);
            }
        } catch (IOException e) {
            System.out.println("ClientChatThread Error: " + e.getMessage());
        } finally {
            chats.remove(socketAddress);
        }
    }

    public void sendMessage(String text){
        output.print(text);
        chatHistory.add(text);

    }

    public List<String> getChatHistory(){
        return chatHistory;
    }

    public void close() throws IOException {
        socket.close();
    }
}
