package com.example.p2pchatproject.serverclient.Client;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientChatThread extends Thread{

    Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    List<String> chatHistory;

    public ClientChatThread(Socket socket) throws IOException {
        this.socket = socket;
        output = new PrintWriter(socket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run(){
        while(true){
            try {
                chatHistory.add(input.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(String text){
        output.print(text);
        chatHistory.add(text);

    }

    public List<String> getChatHistory(){
        return chatHistory;
    }
}
