package com.bsuir.chat.client.logic;

import javafx.scene.control.TextArea;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ClientConnection extends Thread {
    private final InetAddress ip;
    private final int port;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private TextArea chatTextArea;

    public ClientConnection(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(ip, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                StandardCharsets.UTF_8));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
                StandardCharsets.UTF_8));
    }

    public void setChatTextArea(TextArea chatTextArea) {
        this.chatTextArea = chatTextArea;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted() && !socket.isClosed()) {
                String response = bufferedReader.readLine();
                if (response == null) {
                    socket.close();
                    break;
                }
                chatTextArea.setText(chatTextArea.getText() + response + "\n");
            }
        } catch (SocketException ignore) {
            //socket closed
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!socket.isClosed()) {
                try {
                    disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void send(String username, String message) throws IOException {
        bufferedWriter.write(username + ": " + message + "\r\n");
        bufferedWriter.flush();
    }

    public void disconnect() throws IOException {
        socket.close();
        socket.shutdownInput();
        socket.shutdownOutput();
        this.interrupt(); //for sure
    }

}
