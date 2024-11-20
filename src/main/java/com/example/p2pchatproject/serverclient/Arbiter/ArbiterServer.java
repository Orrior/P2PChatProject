package com.example.p2pchatproject.serverclient.Arbiter;

import com.example.p2pchatproject.model.ServerData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Hashtable;

public class ArbiterServer {
    public static void main(String[] args) {
        int port = 6868;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("The server is listening on port " + port);
            Hashtable<SocketAddress, ServerData> addressPool = new Hashtable<>();

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                new ArbiterThread(socket, addressPool).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
