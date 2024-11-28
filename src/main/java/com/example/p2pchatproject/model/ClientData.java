package com.example.p2pchatproject.model;

public class ClientData extends ServerData{
    public boolean pendingConnection;

    public ClientData(ServerData serverData, boolean pendingConnection){
        super(serverData.socketAddress, serverData.data);
        this.pendingConnection = pendingConnection;
    }
}
