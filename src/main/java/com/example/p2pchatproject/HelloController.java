package com.example.p2pchatproject;

import com.example.p2pchatproject.model.DataSocket;
import com.example.p2pchatproject.serverclient.Client.Client;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import java.util.List;

public class HelloController {

    private Client client;

    @FXML
    private ScrollPane usersList;

    @FXML
    protected void onHelloButtonClick() {

        List<DataSocket> connections = client.ping();

        usersList.setContent(createConnectionGrid(connections));
    }

    private GridPane createConnectionGrid(List<DataSocket> connections) {

        //TODO!!! Exclude own connection from list.

        GridPane grid = new GridPane();

        for(int i=0; i<connections.size(); i++){
            Button button = new Button();
            button.setText("Connect");

            Label label = new Label();
            label.setText(connections.get(i).toString());

            //add them to the GridPane
            grid.add(button, 0, i); //  (child, columnIndex, rowIndex)
            grid.add(label , 1, i);

            // margins are up to your preference
            GridPane.setMargin(button, new Insets(5));
            GridPane.setMargin(label, new Insets(5));
        }

        return grid;
    }

    public void setClient(Client client){
        this.client = client;
    }
}