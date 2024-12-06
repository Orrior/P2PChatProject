package com.example.p2pchatproject;

import com.example.p2pchatproject.serverclient.Client.Client;
import com.example.p2pchatproject.serverclient.Client.ClientListenerI;
import com.example.p2pchatproject.ui.HelloController;
import com.example.p2pchatproject.util.Util;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketAddress;

public class HelloApplication extends Application {
    private Client client;

    @Override
    public void start(Stage stage) throws IOException {

        //Get resource properties
        String hostname = Util.getProperty("hostname");
        String port = Util.getProperty("port");

        if(port == null || hostname == null){
            throw new FileNotFoundException("Port or hostname in client.properties is missing!");
        }

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