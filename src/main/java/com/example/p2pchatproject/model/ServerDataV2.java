package com.example.p2pchatproject.model;

import java.io.Serializable;
import java.net.SocketAddress;

public record ServerDataV2(SocketAddress socketAddress, String id, String name) implements Serializable {

    @Override
    public String toString() {
        return "ServerDataV2[" +
                "socketAddress=" + socketAddress + ", " +
                "id=" + id + ", " +
                "name=" + name + ']';
    }
}
