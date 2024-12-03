package com.example.p2pchatproject.serverclient.Client;


import com.example.p2pchatproject.model.ServerDataV2;
import com.example.p2pchatproject.util.Util;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public ClientConnectionThread connectionListener;

    private PrintWriter output;
    private ObjectInputStream input;

    private ServerSocket inviteServerSocket;
    private final String username; //TODO! Make this smh changeable.
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
        outputObject.writeObject(new ServerDataV2(inviteAddress, uuid, username));

        //Read first response
        System.out.println("Initial connection addresses: ");
        for (ServerDataV2 dataSocket : (List<ServerDataV2>) this.input.readObject()) {
            System.out.println(dataSocket); // TODO this is actually not very good.
        }
    }

    private List<ServerDataV2> getClientData() throws IOException, ClassNotFoundException {
        List<ServerDataV2> serverData;

        serverData = (List<ServerDataV2>) input.readObject();

        return serverData;
    }

    public List<ServerDataV2> ping() {
        List<ServerDataV2> clientData;
        output.println(username);  // Send something to server to get users list.

        try {
            clientData = getClientData();
            clientData.removeIf(x -> x.socketAddress().equals(inviteServerSocket.getLocalSocketAddress())); //TODO! Ideally ping data should be in hashmap.
            return clientData;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
