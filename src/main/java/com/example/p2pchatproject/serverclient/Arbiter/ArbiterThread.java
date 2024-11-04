package com.example.p2pchatproject.serverclient.Arbiter;


import com.example.p2pchatproject.model.DataSocket;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;

public class ArbiterThread extends Thread{;
    private Hashtable<SocketAddress, DataSocket> addressPool;
    private Socket socket;
    private SocketAddress address;

    public ArbiterThread(Socket socket, Hashtable<SocketAddress, DataSocket> addressPool) {
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

            DataSocket data = (DataSocket) inputObject.readObject();
            addressPool.put(socket.getRemoteSocketAddress(), data);

            for (DataSocket dataSocket : addressPool.values()) {
                System.out.println("DATA: " + dataSocket.socketAddress + " " + dataSocket.data);
            }

            text = "HELLO, CHAT!"; // TODO UWU
            objectOutput.writeObject(new ArrayList<>(addressPool.values()));

            while(!text.equals("fin")) {
                text = reader.readLine();
                objectOutput.reset();
                objectOutput.writeObject(new ArrayList<>(addressPool.values()));
            }

            socket.close();
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
