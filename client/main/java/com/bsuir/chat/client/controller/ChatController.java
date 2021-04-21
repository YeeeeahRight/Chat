package com.bsuir.chat.client.controller;

import com.bsuir.chat.client.logic.ClientConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.SocketException;

public class ChatController {

    @FXML
    private Label serverInfo;

    @FXML
    private Button disconnectButton;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea messageArea;

    @FXML
    private Label usernameInfo;

    @FXML
    private TextArea chatLabel;

    private static String serverInfoStr;
    private static String username;

    private static ClientConnection clientConnection;

    static void setServerInfo(String ip, String port) {
        serverInfoStr = ip + ":" + port;
    }

    static void setUsernameInfo(String username) {
        ChatController.username = username;
    }

    static void setClientConnection(ClientConnection clientConnection) {
        ChatController.clientConnection = clientConnection;
    }

    @FXML
    private void initialize() {
        serverInfo.setText(serverInfoStr);
        usernameInfo.setText(username);

        clientConnection.setChatTextArea(chatLabel);

        sendButton.setOnAction(event -> {
            String message = messageArea.getText();
            if (message == null || message.isEmpty()) {
                alertError("Fill message field");
                return;
            }
            try {
                clientConnection.send(username, message);
                messageArea.clear();
            } catch (IOException e) {
                alertError("The server was shut down");
            }
        });

        disconnectButton.setOnAction(event -> {
            try {
                clientConnection.disconnect();
            } catch (SocketException ignore) {
                //socket closed
            } catch (IOException e) {
                alertError(e.getMessage());
            } finally {
                ((Stage)disconnectButton.getScene().getWindow()).close();
            }
        });

        clientConnection.start();
    }

    private void alertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.show();
    }
}
