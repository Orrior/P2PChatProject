package com.example.p2pchatproject;

import com.example.p2pchatproject.serverclient.Client.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private Client client;

    @Override
    public void start(Stage stage) throws IOException {
        this.client = new Client();
        client.run();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        scene.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());
        HelloController controller = fxmlLoader.getController();
        controller.setClient(client);

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