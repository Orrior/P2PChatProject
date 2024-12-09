package com.example.p2pchatproject.model;

import java.io.Serializable;
import java.net.SocketAddress;

public record ServerData(String id, String name, SocketAddress socketAddress) implements Serializable {

    @Override
    public String toString() {
        return "ServerDataV2[" +
                "socketAddress=" + socketAddress + ", " +
                "id=" + id + ", " +
                "name=" + name + ']';
    }
}
