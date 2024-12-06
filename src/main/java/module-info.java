module com.example.p2pchatproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires jdk.jshell;

    opens com.example.p2pchatproject to javafx.fxml;
    exports com.example.p2pchatproject;
    exports com.example.p2pchatproject.ui;
    opens com.example.p2pchatproject.ui to javafx.fxml;
}