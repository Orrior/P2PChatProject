package com.example.p2pchatproject.ui;

import com.example.p2pchatproject.model.MessageData;
import com.example.p2pchatproject.model.ServerData;
import com.example.p2pchatproject.serverclient.Client.Client;
import com.example.p2pchatproject.serverclient.Client.ClientChatThread;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.SocketAddress;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HelloController {

    private Client client;

    private SocketAddress chatSocketAddress;

    private final Set<SocketAddress> favourites = new HashSet<>();

    @FXML
    private ScrollPane usersList;

    @FXML
    private TextArea userChat;

    @FXML
    private TextField userEntry;

    @FXML
    private Button sendMessageButton;

    @FXML
    public void onHelloButtonClick() {
        if (sendMessageButton.isDisable() && chatSocketAddress != null) {
            sendMessageButton.setDisable(false);
        }

        List<ServerData> connections = client.ping();

        usersList.setContent(createVbox(connections));
    }

    @FXML
    protected void tryConnectButtonClick(SocketAddress socketAddress) {
        client.connectionListener.requestChatConnect(socketAddress);
        onHelloButtonClick();
    }

    @FXML
    protected void sendMessageOnClick() {
        if (chatSocketAddress == null) {
            return;
        }

        client.connectionListener.getChat(chatSocketAddress).sendMessage(userEntry.getText());
        userEntry.clear();
        refreshChat();
    }

    @FXML
    public void onMessageRefresh(SocketAddress socketAddress) {
        if (socketAddress.equals(this.chatSocketAddress)) {
            refreshChat();
        }
        onHelloButtonClick();
    }

    @FXML
    protected void setChat(SocketAddress socketAddress) {
        this.chatSocketAddress = socketAddress;
        refreshChat();
        onHelloButtonClick();
    }

    protected void acceptPendingConnection(SocketAddress address) {
        client.connectionListener.acceptPendingConnection(address);
        setChat(address);
        onHelloButtonClick();
    }

    protected void rejectPendingConnection(SocketAddress address) {
        client.connectionListener.rejectPendingConnection(address);
        onHelloButtonClick();
    }

    private void refreshChat() {
        userChat.setText(createChatTextBox());
    }

    private void favourite(SocketAddress socketAddress) {
        if (favourites.contains(socketAddress)) {
            favourites.remove(socketAddress);
        } else {
            favourites.add(socketAddress);
        }
        onHelloButtonClick();
    }

    private String createChatTextBox() {
        List<MessageData> chatHistory = client.connectionListener.getChat(chatSocketAddress).getChatHistory();
        StringBuffer chat = new StringBuffer();
        chatHistory.forEach(x-> chat.append(x.name()).append(":"). append(x.message()).append("\n"));
        return chat.toString(); //TODO chat is this real?
    }

    private VBox createVbox (List<ServerData> connections) {
        VBox vbox = new VBox();
        VBox.setMargin(vbox, new Insets(5));

        connections.sort(Comparator.comparing(x -> !favourites.contains(x.socketAddress())));
        for (ServerData connection : connections) {
            vbox.getChildren().add(createRow(connection));
        }

        return vbox;
    }

    private HBox createRow (ServerData connection) {
        HBox hbox = new HBox(8);

        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getStyleClass().add("row");

        SocketAddress socketAddress = connection.socketAddress();

        if (!client.connectionListener.chatsContains(socketAddress)) {
            // Nothing ever happens, we can ask if user wants to chat with us.
            hbox.getChildren().add(requestConnectButton(socketAddress));
        } else {
            ClientChatThread chatThread = client.connectionListener.getChat(socketAddress);
            if (chatThread.isPending && chatThread.getChatHistory().isEmpty()) {
                // We are requested to accept/reject chat.
                hbox.getChildren().add(acceptConnectionButton(socketAddress));
                hbox.getChildren().add(rejectConnectionButton(socketAddress));
            } else if (!chatThread.isPending && !chatThread.getChatHistory().isEmpty()) {
                // Chat is ongoing
                hbox.getChildren().add(chatButton(socketAddress));
                hbox.getChildren().add(disconnectButton(socketAddress));
            } else if (!chatThread.isPending && chatThread.getChatHistory().isEmpty()) {
                // Chat is waiting to be accepted/rejected by other side.
                hbox.getChildren().add(requestConnectionPendingButton());
            } else {
                throw new RuntimeException("Unexpected conditions in HelloController");
            }
        }
        Label label = new Label();
        label.setText(connection.name());
        hbox.getChildren().add(label);

        //Context menu
        ContextMenu menu = new ContextMenu();
        MenuItem menuItem = new MenuItem();
        menuItem.setText(
                "Username: " + connection.name() + "\n" +
                "ID: " + connection.id() + "\n" +
                "Address: " + connection.socketAddress());

        menuItem.setOnAction(e -> copyToClipboard(menuItem.getText()));

        menu.getItems().add(menuItem);
        hbox.setOnContextMenuRequested(e -> menu.show(hbox.getScene().getWindow(), e.getScreenX(), e.getScreenY()));

        //Add space so favourite button is always on right side.
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        hbox.getChildren().add(space);
        hbox.getChildren().add(favouriteButton(socketAddress));

        return hbox;
    }

    private Button favouriteButton (SocketAddress socketAddress) {
        Button button = createButton();
        Region icon = new Region();
        icon.getStyleClass().add("favourite-icon");

        if (favourites.contains(socketAddress)) {
            button.getStyleClass().add("unfavourite-button");
        } else {
            button.getStyleClass().add("favourite-button");
        }

        button.setOnAction(x -> favourite(socketAddress));
        button.setGraphic(icon);

        return button;
    }

    private Button chatButton(SocketAddress socketAddress) {
        Button button = createButton();

        button.setText("chat");
        button.setOnAction(x -> setChat(socketAddress));

        return button;
    }

    private Button disconnectButton(SocketAddress socketAddress) {
        Button button = createButton();

        button.setText("disconnect");
        button.setOnAction(x -> client.connectionListener.closeChat(socketAddress));

        return button;
    }

    private Button acceptConnectionButton(SocketAddress socketAddress) {
        Button button = createButton();

        button.setText("Accept");
        button.setOnAction(x -> acceptPendingConnection(socketAddress));

        return button;
    }

    private Button rejectConnectionButton(SocketAddress socketAddress) {
        Button button = createButton();

        button.setText("Reject");
        button.setOnAction(x -> rejectPendingConnection(socketAddress));

        return button;
    }

    private Button requestConnectButton(SocketAddress socketAddress) {
        Button button = createButton();

        button.setText("Connect");
        button.setOnAction(x -> tryConnectButtonClick(socketAddress));

        return button;
    }

    private Button requestConnectionPendingButton() {
        Button button = createButton();
        button.setText("Pending...");
        button.setDisable(true);

        return button;
    }

    private Button createButton() {
        Button button = new Button();
        button.getStyleClass().add("row-button");

        return button;
    }

    private void copyToClipboard(String data) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(data);
        clipboard.setContent(content);
    }

    public void onDisconnect() {
        chatSocketAddress = null;
        sendMessageButton.setDisable(true);
    }

    public void setClient(Client client) {
        this.client = client;
    }
}