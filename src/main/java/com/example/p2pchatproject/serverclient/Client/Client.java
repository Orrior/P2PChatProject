package com.example.p2pchatproject.serverclient.Client;


import com.example.p2pchatproject.model.ClientData;
import com.example.p2pchatproject.model.ServerData;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public ClientConnectionThread connectionListener;

    private PrintWriter output;
    private ObjectInputStream input;

    private ServerSocket inviteServerSocket;
    private Boolean eof;
    private final String username = "Client-1"; //TODO! Make this smh changeable.


    public static void main(String[] args) {
        Client client = new Client();
        client.run();
        client.chat();
    }

    public void chat() {
        String result = null;
        Scanner scanner = new Scanner(System.in);

        while (!eof) {
            System.out.println("ENTER TEXT: ");
            result = scanner.nextLine();
            output.println(result);

            List<ServerData> dataPool = null;
            try {
                dataPool = (List<ServerData>) input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            for (ServerData dataSocket : dataPool) {
                System.out.println("DATA: " + dataSocket.socketAddress + " " + dataSocket.data);
            }
        }
    }

    public void run() {
        eof = false;

        String hostname = "localhost";
        int port = 6868;

        try {
            // Create socket listening to chat P2P connection invitations
            inviteServerSocket = new ServerSocket(0); // port 0 will try to listen and take any free port.
            SocketAddress inviteAddress = inviteServerSocket.getLocalSocketAddress(); // TODO! Maybe we should bind ServerSocket ip to localhost later.
            System.out.println("INVITE SERVER LISTENING ON PORT " + inviteAddress.toString());

            // Start Connection with Arbiter Server, initialise fields.
            initArbiterSocket(hostname, port, inviteAddress);
            System.out.println("Connection Established!");

            // Create chat P2P connection invitations listener
            connectionListener = new ClientConnectionThread(inviteServerSocket);
            connectionListener.start();

        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    private void initArbiterSocket(String hostname, int port, SocketAddress inviteAddress) throws IOException, ClassNotFoundException {
        Socket arbiterSocket = new Socket(hostname, port);

        //OUT-COMING MESSAGE
        OutputStream output = arbiterSocket.getOutputStream();
        ObjectOutputStream outputObject = new ObjectOutputStream(output);
        PrintWriter writer = new PrintWriter(output, true);

        //IN-COMING MESSAGE
        InputStream input = arbiterSocket.getInputStream();
        ObjectInputStream inputObject = new ObjectInputStream(input);

        this.output = writer;
        this.input = inputObject;

        //First connection data;
        outputObject.writeObject(new ServerData(inviteAddress, username));
        for (ServerData dataSocket : (List<ServerData>) this.input.readObject()) {
            System.out.println(dataSocket); // TODO this is actually not very good.
        }
    }

    private List<ClientData> getClientData() throws IOException, ClassNotFoundException {
        List<ServerData> serverData;
        List<ClientData> clientData = new ArrayList<>();

        serverData = (List<ServerData>) input.readObject();


        serverData.forEach(x -> clientData.add(new ClientData(x,
                connectionListener.pendingSockets.containsKey(x.socketAddress))));
        return clientData;
    }

    public List<ClientData> ping() {
        List<ClientData> clientData;
        output.println(username);  // Send something to server to get users list.

        try {
            clientData = getClientData();
            clientData.removeIf(x -> x.socketAddress.equals(inviteServerSocket.getLocalSocketAddress())); //TODO! Ideally ping data should be in hashmap.
            return clientData;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void connectListener(SocketAddress socketAddress) {
        // DEBUG DEPRECATED
        try (Socket listenerSocket = new Socket()) {
            listenerSocket.connect(socketAddress);
            PrintWriter writer = new PrintWriter(listenerSocket.getOutputStream(), true);
            writer.println("TEST123");
            System.out.println("Connection Confirmed!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
