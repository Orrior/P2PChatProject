package com.example.p2pchatproject.model;

import java.io.Serializable;
import java.net.Socket;

public record ClientData(String id, String name, Socket socket) implements Serializable {

    @Override
    public String toString() {
        return "ClientDataV2[" +
                "name=" + name + ", " +
                "socket=" + socket + ']';
    }

}
