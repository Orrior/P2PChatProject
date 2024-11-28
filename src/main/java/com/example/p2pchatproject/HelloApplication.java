package com.example.p2pchatproject;

import com.example.p2pchatproject.serverclient.Client.Client;
import com.example.p2pchatproject.serverclient.Client.ClientListenerI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.util.Properties;
import java.util.UUID;

public class HelloApplication extends Application {
    private Client client;

    @Override
    public void start(Stage stage) throws IOException {
        this.client = new Client();
        client.run();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 400);
        scene.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());
        HelloController controller = fxmlLoader.getController();
        controller.setClient(client);
        // Add listener.
        client.connectionListener.addPendingConnectionListener(new ClientListenerI() {
            @Override
            public void onConnection() {
                controller.onHelloButtonClick();
            }

            @Override
            public void onMessage(SocketAddress socketAddress) {
                // Update Connections Part
                controller.onMessageRefresh(socketAddress);
                // Update Chat part
            }

            @Override
            public void onDisconnect(){
                controller.onDisconnect();
                controller.onHelloButtonClick();
            }
        });

        stage.setTitle("P2PChat");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        client.connectionListener.close();
    }

    public static void main(String[] args) {
        launch();
    }
}