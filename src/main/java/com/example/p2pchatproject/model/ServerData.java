package com.example.p2pchatproject.model;

import java.io.Serializable;
import java.net.SocketAddress;

public class ServerData implements Serializable {
    public SocketAddress socketAddress;
    public String data;

    public ServerData(SocketAddress socketAddress, String data){
        this.socketAddress = socketAddress;
        this.data = data;
    }

    @Override
    public String toString() {
        return "DATA: " + socketAddress + " | " + data;
    }
}
