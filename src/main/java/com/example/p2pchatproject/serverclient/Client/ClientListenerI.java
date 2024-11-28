package com.example.p2pchatproject.serverclient.Client;

import java.net.SocketAddress;

public interface ClientListenerI {
    void onConnection();

    void onMessage(SocketAddress socketAddress);

    void onDisconnect();
}
