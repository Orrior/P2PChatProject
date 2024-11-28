package com.example.p2pchatproject;

import com.example.p2pchatproject.model.ClientData;
import com.example.p2pchatproject.serverclient.Client.Client;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.SocketAddress;
import java.util.List;

public class HelloController {

    private Client client;

    private SocketAddress chatSocketAddress;

    @FXML
    private ScrollPane usersList;

    @FXML
    private TextArea userChat;

    @FXML
    private TextField userEntry;

    @FXML
    private Button sendMessageButton;

    @FXML
    protected void onHelloButtonClick() {
        if(sendMessageButton.isDisable() && chatSocketAddress != null){
            sendMessageButton.setDisable(false);
        }

        List<ClientData> connections = client.ping();

        usersList.setContent(createConnectionGrid(connections));
    }

    @FXML
    protected void tryConnectButtonClick(SocketAddress socketAddress){
        client.connectionListener.requestChatConnect(socketAddress);
        onHelloButtonClick();
    }

    @FXML
    protected void sendMessageOnClick(){
        if(chatSocketAddress == null){
            return;
        }

        client.connectionListener.getChat(chatSocketAddress).sendMessage(userEntry.getText());
        userEntry.setText(""); // TODO This is unholy
        refreshChat();
    }

    @FXML
    protected void onMessageRefresh(SocketAddress socketAddress){
        if(socketAddress.equals(this.chatSocketAddress)){
            refreshChat();
        }
        onHelloButtonClick();
    }

    @FXML
    protected void setChat(SocketAddress socketAddress){
        this.chatSocketAddress = socketAddress;
        refreshChat();
        onHelloButtonClick();
    }

    protected void acceptPendingConnection(SocketAddress address){
        client.connectionListener.acceptPendingConnection(address);
        setChat(address);
        onHelloButtonClick();
    }

    protected void rejectPendingConnection(SocketAddress address){
        client.connectionListener.rejectPendingConnection(address);
        onHelloButtonClick();
    }

    private void refreshChat(){
        userChat.setText(createChatTextBox());
    }

    private String createChatTextBox(){
        List<String> chatHistory = client.connectionListener.getChat(chatSocketAddress).getChatHistory();
        return String.join("\n", chatHistory);
    }

    private GridPane createConnectionGrid(List<ClientData> connections) {
        GridPane grid = new GridPane();
        GridPane.setMargin(grid, new Insets(5));

        for(int i=0; i<connections.size(); i++){
            SocketAddress socketAddress = connections.get(i).socketAddress;
            if (connections.get(i).pendingConnection) {
                // Incoming chat request

                Button button2 = new Button();
                button2.setText("Accept");
                button2.setOnAction(x -> acceptPendingConnection(socketAddress));

                Button button3 = new Button();
                button3.setText("Reject");
                button3.setOnAction(x -> rejectPendingConnection(socketAddress));

                grid.add(button2, 1, i); // FIXME
                grid.add(button3, 2, i); // FIXME
            } else {
                Button button1 = new Button();

                if(client.connectionListener.chatsContains(socketAddress)){
                    if(!client.connectionListener.getChat(socketAddress).getChatHistory().isEmpty()){
                        // Chat is ongoing
                        button1.setText("chat");
                        button1.setOnAction(x -> setChat(socketAddress));
                        //TODO CHAT
                    } else {
                        // Chat is waiting to be accepted/rejected
                        button1.setDisable(true);
                        button1.setText("request pending...");
                    }
                } else {
                    // Nothing ever happens, we can ask if user wants to chat with us.
                    button1.setText("Connect");
                    button1.setOnAction(x -> tryConnectButtonClick(socketAddress));
                }
                grid.add(button1, 0, i); //  (child, columnIndex, rowIndex)
            }


            Label label = new Label();
            label.setText(connections.get(i).toString());

            //add connection button to GridPane

            grid.add(label , 3, i);

            // margins are up to your preference
        }

        return grid;
    }

    public void onDisconnect(){
        chatSocketAddress = null;
        sendMessageButton.setDisable(true);
    }

    public void setClient(Client client){
        this.client = client;
    }
}