package com.example.p2pchatproject.serverclient.Client;

import com.example.p2pchatproject.model.DataSocket;
import com.example.p2pchatproject.serverclient.Arbiter.ArbiterThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Hashtable;

public class ClientConnectionListener extends Thread {

    ServerSocket listenerServerSocket;

    public ClientConnectionListener(ServerSocket serverSocket){
        this.listenerServerSocket = serverSocket;
    }


    public void run(){
        System.out.println("The ConnectionListener is listening on port " + listenerServerSocket.getLocalPort());
        try {
            while (true) {

                    Socket socket = listenerServerSocket.accept();
                    System.out.println("New client connected");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    System.out.println(reader.readLine());
                    socket.close();
            }
        } catch (Exception e) {
            System.out.println("CONNECTIONLISTENER ERROR: " + e.getMessage());
        }
    }
}
