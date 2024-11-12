package com.example.p2pchatproject.serverclient.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class ClientConnectionListener extends Thread {

    ServerSocket listenerServerSocket;
    List<ClientChatThread> chats;
    Hashtable<SocketAddress, Socket> pendingSockets;


    public ClientConnectionListener(ServerSocket serverSocket){
        this.listenerServerSocket = serverSocket;
    }

    public void close() throws IOException {
        listenerServerSocket.close();
    }

    public void run(){
        System.out.println("The ConnectionListener is listening on port " + listenerServerSocket.getLocalPort());
        try {
            while (true) {

                Socket socket = listenerServerSocket.accept();
                SocketAddress address = socket.getRemoteSocketAddress();
                pendingSockets.put(address, socket);

                System.out.println("New chat request");

                boolean acceptChatOrNot = true; // Todo Handle this bozo

                resolveChat(address, acceptChatOrNot);
            }
        } catch (Exception e) {
            System.out.println("CONNECTIONLISTENER ERROR: " + e.getMessage());
        }
    }

    public void resolveChat(SocketAddress address, boolean accept) throws IOException {
        Socket socket = pendingSockets.get(address);
        if(accept){
            new PrintWriter(socket.getOutputStream()).print("hiii <3<3<3 meowdy everybunny");
            chats.add(new ClientChatThread(socket));
        } else {
            socket.close();
        }
        pendingSockets.remove(address);
    }

    public void requestChatConnect(SocketAddress socketAddress) {
        try {
            Socket listenerSocket = new Socket();
            listenerSocket.connect(socketAddress);
            new PrintWriter(listenerSocket.getOutputStream()).print("hallo"); //TODO make hallo message nickname;
            new BufferedReader(new InputStreamReader(listenerSocket.getInputStream())).read();
            chats.add(new ClientChatThread(listenerSocket));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
