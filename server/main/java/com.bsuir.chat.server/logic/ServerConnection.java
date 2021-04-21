package com.bsuir.chat.server.logic;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ServerConnection extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private BufferedWriter outputStream;

    public ServerConnection(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (SocketException ignored) {
            //socket closed
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            server.removeConnection(this);
        }
    }

    private void handleClientSocket() throws IOException {
        outputStream = new BufferedWriter(new OutputStreamWriter(
                clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                clientSocket.getInputStream(), StandardCharsets.UTF_8));
        fillHistory();
        String message;
        while (!clientSocket.isClosed() && !isInterrupted() &&
                (message = reader.readLine()) != null) {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String currentDate = simpleDateFormat.format(date);
            StringBuilder serverMessageBuilder = new StringBuilder();
            serverMessageBuilder.append(currentDate).append(" ").append(message).append("\r\n");
            while (reader.ready()) {
                serverMessageBuilder.append(reader.readLine()).append("\r\n");
            }
            server.addHistoryMessage(serverMessageBuilder.toString());
            sendMessageToAll(serverMessageBuilder.toString());
        }
    }

    private void fillHistory() {
        String message = server.getChatHistory();
        if (!message.isEmpty()) {
            sendMessage(message);
        }
    }

    private void sendMessageToAll(String message) {
        for (ServerConnection serverConnection : server.getConnections()) {
            serverConnection.sendMessage(message);
        }
    }

    private void sendMessage(String message) {
        try {
            outputStream.write(message);
            outputStream.flush();
        } catch (IOException ignore) {
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerConnection that = (ServerConnection) o;

        if (!Objects.equals(clientSocket, that.clientSocket)) return false;
        if (!Objects.equals(server, that.server)) return false;
        return Objects.equals(outputStream, that.outputStream);
    }

    @Override
    public int hashCode() {
        int result = clientSocket != null ? clientSocket.hashCode() : 0;
        result = 31 * result + (server != null ? server.hashCode() : 0);
        result = 31 * result + (outputStream != null ? outputStream.hashCode() : 0);
        return result;
    }
}
