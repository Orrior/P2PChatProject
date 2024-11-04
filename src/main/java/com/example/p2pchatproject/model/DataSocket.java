package com.example.p2pchatproject.model;

import java.io.Serializable;
import java.net.Socket;
import java.net.SocketAddress;

public class DataSocket implements Serializable {
    public SocketAddress socketAddress;
    public String data;

    public DataSocket(SocketAddress socket, String data){
        this.socketAddress = socket;
        this.data = data;
    }

    @Override
    public String toString() {
        return "DATA: " + socketAddress + " | " + data;
    }
}
