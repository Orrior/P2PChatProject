module com.example.p2pchat {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires jdk.jshell;

    opens com.example.p2pchat to javafx.fxml;
    exports com.example.p2pchat;
    exports com.example.p2pchat.ui;
    exports com.example.p2pchat.util;
    exports com.example.p2pchat.model;
    exports com.example.p2pchat.serverclient.Client;
    exports com.example.p2pchat.serverclient.Server;
    opens com.example.p2pchat.ui to javafx.fxml;
}