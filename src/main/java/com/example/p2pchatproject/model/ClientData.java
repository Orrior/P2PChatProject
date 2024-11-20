package com.example.p2pchatproject.model;

import java.net.SocketAddress;

public class ClientData extends ServerData{
    public boolean pendingConnection;

    public ClientData(SocketAddress socketAddress, String data) {
        super(socketAddress, data);
    }

    public ClientData(ServerData serverData){
        super(serverData.socketAddress, serverData.data);
    }

    public ClientData(ServerData serverData, boolean pendingConnection){
        super(serverData.socketAddress, serverData.data);
        this.pendingConnection = pendingConnection;
    }
}
