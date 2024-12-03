package com.example.p2pchatproject.serverclient.Server;


import com.example.p2pchatproject.model.ServerDataV2;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;

public class ServerThread extends Thread{;
    private Hashtable<SocketAddress, ServerDataV2> addressPool;
    private Socket socket;
    private SocketAddress address;

    public ServerThread(Socket socket, Hashtable<SocketAddress, ServerDataV2> addressPool) {
        this.address = socket.getRemoteSocketAddress();
        this.socket = socket;
        this.addressPool = addressPool;
    }

    public void run() {
        try {
            String text;

            InputStream input = socket.getInputStream();
            ObjectInputStream inputObject = new ObjectInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);

            ServerDataV2 data = (ServerDataV2) inputObject.readObject();
            addressPool.put(socket.getRemoteSocketAddress(), data);

            for (ServerDataV2 dataSocket : addressPool.values()) {
                System.out.println(dataSocket.socketAddress() +"|"+ dataSocket.id() +"|" + dataSocket.name());
            }


            objectOutput.writeObject(new ArrayList<>(addressPool.values()));

            while(true) {
                reader.readLine();
                objectOutput.reset();
                objectOutput.writeObject(new ArrayList<>(addressPool.values()));
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
//            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            addressPool.remove(socket.getRemoteSocketAddress());
        }
    }
}
