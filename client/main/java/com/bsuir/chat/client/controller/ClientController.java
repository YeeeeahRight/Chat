package com.bsuir.chat.client.controller;

import com.bsuir.chat.client.loader.AppLoader;
import com.bsuir.chat.client.logic.ClientConnection;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientController {

    @FXML
    private TextField IPField;

    @FXML
    private TextField portField;

    @FXML
    private TextField usernameField;

    @FXML
    private Button connectButton;

    @FXML
    void initialize() {
        connectButton.setOnAction(event -> {
            InetAddress inetAddress = readAddress(IPField.getText());
            if (inetAddress == null) {
                return;
            }
            int port = readPort(portField.getText());
            if (port == -1) {
                return;
            }
            String username = usernameField.getText();
            if (username == null || username.isEmpty()) {
                alertError("Fill username field");
                return;
            }
            if (username.length() > 20) {
                alertError("Username max length is 20");
                return;
            }
            ClientConnection clientConnection = new ClientConnection(inetAddress, port);
            try {
                clientConnection.connect();
                createChatForm(clientConnection);
            } catch (IOException e) {
                alertError("Server not found");
            }
        });
    }

    private InetAddress readAddress(String addressStr) {
        if (addressStr == null || addressStr.isEmpty()) {
            alertError("Fill IP field");
            return null;
        }
        try {
            return InetAddress.getByName(addressStr);
        } catch (UnknownHostException e) {
            alertError("Unknown IP");
        }
        return null;
    }

    private int readPort(String portStr) {
        if (portStr == null || portStr.isEmpty()) {
            alertError("Fill port field");
            return -1;
        }
        int port;
        try {
            port = Integer.parseInt(portStr);
            if (port >= 0 && port <= 65536) {
                return port;
            } else {
                alertError("Port should be in range of 2 bytes: [0, 65536]");
            }
        } catch (NumberFormatException e) {
            alertError("Port is not number");
        }
        return -1;
    }

    private void alertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.show();
    }

    private void createChatForm(ClientConnection clientConnection) {
        try {
            ChatController.setClientConnection(clientConnection);
            ChatController.setServerInfo(IPField.getText(), portField.getText());
            ChatController.setUsernameInfo(usernameField.getText());
            Scene scene = new Scene(AppLoader.loadFXML("chat.fxml"));
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initOwner(AppLoader.getStage());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Chat");
            stage.showAndWait();
        } catch (IOException e) {
            alertError("Can't create chat window");
        }
    }
}
