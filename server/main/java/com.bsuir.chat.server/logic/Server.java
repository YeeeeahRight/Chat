package com.bsuir.chat.server.logic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server extends Thread {
    private final InetAddress ip;
    private final int port;
    private final int maxUsers;
    private final List<ServerConnection> connections;
    private final StringBuilder chatHistory = new StringBuilder();

    private ServerSocket serverSocket;

    public Server(InetAddress ip, int port, int maxUsers) {
        this.ip = ip;
        this.port = port;
        this.maxUsers = maxUsers;
        this.connections = new ArrayList<>();
    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port, maxUsers, ip);
        start();
    }

    @Override
    public void run() {
        if (serverSocket != null && serverSocket.isBound()) {
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ServerConnection connection = new ServerConnection(this, clientSocket);
                    connections.add(connection);
                    connection.start();
                } catch (SocketException ignore) {
                    //closed client socket on server
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopServer() throws IOException {
        Iterator<ServerConnection> serverConnectionIterator = connections.iterator();
        while (serverConnectionIterator.hasNext()) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            serverConnectionIterator.remove();
            serverConnection.getClientSocket().shutdownOutput();
            serverConnection.getClientSocket().shutdownInput();
            serverConnection.getClientSocket().close();
        }
        serverSocket.close();
        this.interrupt(); //useless but for sure
    }

    List<ServerConnection> getConnections() {
        return connections;
    }

    void removeConnection(ServerConnection serverConnection) {
        connections.remove(serverConnection);
    }

    void addHistoryMessage(String message) {
        chatHistory.append(message);
    }

    String getChatHistory() {
        return chatHistory.toString();
    }
}
