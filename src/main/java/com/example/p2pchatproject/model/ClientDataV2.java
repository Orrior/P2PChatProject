package com.example.p2pchatproject.model;

import java.io.Serializable;
import java.net.Socket;
import java.net.SocketAddress;

public record ClientDataV2(String name, String id, Socket socket) implements Serializable {

    @Override
    public String toString() {
        return "ClientDataV2[" +
                "name=" + name + ", " +
                "socket=" + socket + ']';
    }

}
