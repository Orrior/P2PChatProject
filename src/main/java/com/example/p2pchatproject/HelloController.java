package com.example.p2pchatproject;

import com.example.p2pchatproject.model.ClientData;
import com.example.p2pchatproject.model.ServerData;
import com.example.p2pchatproject.serverclient.Client.Client;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import java.net.SocketAddress;
import java.util.List;

public class HelloController {

    private Client client;

    @FXML
    private Label test123;

    @FXML
    private ScrollPane usersList;

    @FXML
    protected void onHelloButtonClick() {

        List<ClientData> connections = client.ping();

        usersList.setContent(createConnectionGrid(connections));
    }

    @FXML
    protected void tryConnectButtonClick(SocketAddress socketAddress){
        client.connectionListener.requestChatConnect(socketAddress);
    }

    protected void acceptPendingConnection(SocketAddress address){
        client.connectionListener.acceptPendingConnection(address);
    }

    protected void rejectPendingConnection(SocketAddress address){
        client.connectionListener.rejectPendingConnection(address);
    }

    private GridPane createConnectionGrid(List<ClientData> connections) {
        GridPane grid = new GridPane();

        for(int i=0; i<connections.size(); i++){
            Button button1 = new Button();
            button1.setText("Connect");
            SocketAddress socketAddress = connections.get(i).socketAddress;
            button1.setOnAction(x -> tryConnectButtonClick(socketAddress));

            Label label = new Label();
            label.setText(connections.get(i).toString());

            //add connection button to GridPane
            grid.add(button1, 0, i); //  (child, columnIndex, rowIndex)
            grid.add(label , 3, i);

            if(connections.get(i).pendingConnection){
                Button button2 = new Button();
                button2.setText("Accept");
                button2.setOnAction(x -> acceptPendingConnection(socketAddress));

                Button button3 = new Button();
                button3.setText("Reject");
                button3.setOnAction(x -> rejectPendingConnection(socketAddress));

                grid.add(button2, 1, i); // FIXME
                grid.add(button3, 2, i); // FIXME
            }

            // margins are up to your preference
            GridPane.setMargin(button1, new Insets(5));
            GridPane.setMargin(label, new Insets(5));
        }

        return grid;
    }

    public void setClient(Client client){
        this.client = client;
    }
}