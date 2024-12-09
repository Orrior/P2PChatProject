package com.example.p2pchat.serverclient.Client;

import java.net.SocketAddress;

public interface ClientListenerI {
    void onConnection();

    void onMessage(SocketAddress socketAddress);

    void onDisconnect();
}
