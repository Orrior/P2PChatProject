package com.example.p2pchatproject;

import com.example.p2pchatproject.model.ServerDataV2;
import com.example.p2pchatproject.serverclient.Client.Client;
import com.example.p2pchatproject.serverclient.Client.ClientChatThread;
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

        List<ServerDataV2> connections = client.ping();

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

    private GridPane createConnectionGrid(List<ServerDataV2> connections) {
        GridPane grid = new GridPane();
        GridPane.setMargin(grid, new Insets(5));

        for(int i=0; i<connections.size(); i++) {
            Button button1 = new Button();
            Button button2 = new Button();
            Button button3 = new Button();

            SocketAddress socketAddress = connections.get(i).socketAddress();

            if(!client.connectionListener.chatsContains(socketAddress)) {
                // Nothing ever happens, we can ask if user wants to chat with us.
                button1.setText("Connect");
                button1.setOnAction(x -> tryConnectButtonClick(socketAddress));
                grid.add(button1, 0, i); //  (child, columnIndex, rowIndex)
            } else {
                ClientChatThread chatThread = client.connectionListener.getChat(socketAddress);

                if (chatThread.isPending && chatThread.getChatHistory().isEmpty()){
                    // We are requested to accept/reject chat.
                    button2.setText("Accept");
                    button2.setOnAction(x -> acceptPendingConnection(socketAddress));

                    button3.setText("Reject");
                    button3.setOnAction(x -> rejectPendingConnection(socketAddress));

                    grid.add(button2, 1, i); // FIXME
                    grid.add(button3, 2, i); // FIXME

                } else if (!chatThread.isPending && !chatThread.getChatHistory().isEmpty()) {
                    // Chat is ongoing
                    button1.setText("chat");
                    button1.setOnAction(x -> setChat(socketAddress));

                    grid.add(button1, 0, i);
                } else if (!chatThread.isPending && chatThread.getChatHistory().isEmpty()) {
                    // Chat is waiting to be accepted/rejected by other side.
                    button1.setDisable(true);
                    button1.setText("request pending...");

                    grid.add(button1, 0, i);
                } else {
                    throw new RuntimeException("Unexpected conditions in HelloController");
                }
            }

            Label label = new Label();
            label.setText(connections.get(i).toString());
            grid.add(label , 3, i);
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