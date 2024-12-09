package com.example.p2pchat.serverclient.Client;


import com.example.p2pchat.model.ServerData;
import com.example.p2pchat.util.Util;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public ClientConnectionThread connectionListener;

    private PrintWriter output;
    private ObjectInputStream input;

    private ServerSocket inviteServerSocket;
    private final String username;
    private final String uuid;

    public Client(){
        String[] userdata = Util.getUserData();
        username = userdata[0];
        uuid = userdata[1];
    }
    
    public void run() {
        try {
            // Create socket listening to chat P2P connection invitations
            inviteServerSocket = new ServerSocket(0); // port 0 will try to take any free port.
            SocketAddress inviteAddress = inviteServerSocket.getLocalSocketAddress(); // TODO! Maybe we should bind ServerSocket ip to localhost later.
            System.out.println("Chat server listening on address : " + inviteAddress.toString());

            // Start Connection with Arbiter Server, initialise fields.
            initHubConnection(inviteAddress);
            System.out.println("Connection with hub Server Established!");

            // Create chat P2P connection invitations listener
            connectionListener = new ClientConnectionThread(inviteServerSocket, username, uuid);
            connectionListener.start();

        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    private void initHubConnection(SocketAddress inviteAddress) throws IOException, ClassNotFoundException {
        Socket serverSocket = new Socket(
                Util.getProperty("hostname"),
                Integer.parseInt(Objects.requireNonNull(Util.getProperty("port"))));

        //OUTGOING MESSAGE
        OutputStream output = serverSocket.getOutputStream();
        ObjectOutputStream outputObject = new ObjectOutputStream(output);
        PrintWriter writer = new PrintWriter(output, true);

        //INGOING MESSAGE
        InputStream input = serverSocket.getInputStream();
        ObjectInputStream inputObject = new ObjectInputStream(input);

        this.output = writer;
        this.input = inputObject;

        //Send our data to server list
        outputObject.writeObject(new ServerData(uuid, username, inviteAddress));

        //Read first response
        System.out.println("Initial connection addresses: ");
        for (ServerData dataSocket : (List<ServerData>) this.input.readObject()) {
            System.out.println(dataSocket); // TODO this is actually not very good.
        }
    }

    private List<ServerData> getClientData() throws IOException, ClassNotFoundException {
        List<ServerData> serverData;

        serverData = (List<ServerData>) input.readObject();

        return serverData;
    }

    public List<ServerData> ping() {
        List<ServerData> clientData;
        output.println(username);  // Send something to server to get users list.

        try {
            clientData = getClientData();
            clientData.removeIf(x -> x.socketAddress().equals(inviteServerSocket.getLocalSocketAddress()));
            return clientData;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
