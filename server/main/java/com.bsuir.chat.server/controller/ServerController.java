package com.bsuir.chat.server.controller;

import com.bsuir.chat.server.logic.Server;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerController {
    @FXML
    private TextField IPField;

    @FXML
    private TextField portField;

    @FXML
    private TextField maxUsersField;

    @FXML
    private Button startButton;

    @FXML
    private Button endButton;

    private Server server;

    @FXML
    void initialize() {
        startButton.setOnAction(event -> {
            InetAddress inetAddress = readAddress(IPField.getText());
            if (inetAddress == null) {
                return;
            }
            int port = readPort(portField.getText());
            if (port == -1) {
                return;
            }
            int maxUsers = readMaxUsers(maxUsersField.getText());
            if (maxUsers == -1) {
                return;
            }
            Server server = new Server(inetAddress, port, maxUsers);
            server.setUncaughtExceptionHandler((t, e) -> alertError(e.getMessage()));
            try {
                server.startServer();
                this.server = server;
                startButton.setDisable(true);
                endButton.setDisable(false);
            } catch (IOException e) {
                alertError(e.getMessage());
            }
        });

        endButton.setOnAction(event -> {
            if (server != null) {
                try {
                    server.stopServer();
                } catch (IOException e) {
                    alertError(e.getMessage());
                }
            }
            server = null;
            startButton.setDisable(false);
            endButton.setDisable(true);
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

    private int readMaxUsers(String maxUsersStr) {
        if (maxUsersStr == null || maxUsersStr.isEmpty()) {
            alertError("Fill max users field");
            return -1;
        }
        int maxUsers;
        try {
            maxUsers = Integer.parseInt(maxUsersField.getText());
            if (maxUsers >= 0 && maxUsers <= 100) {
                return maxUsers;
            } else {
                alertError("Max users should be in range: [0, 100]");
            }
        } catch (NumberFormatException e) {
            alertError("Max users is not number");
        }
        return -1;
    }

    private void alertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.show();
    }
}
