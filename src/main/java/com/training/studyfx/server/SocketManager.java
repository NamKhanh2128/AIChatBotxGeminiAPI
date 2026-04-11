package com.training.studyfx.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketManager {
    private static SocketManager instance;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private List<MessageListener> messageListeners = new ArrayList<>();

    private SocketManager() {}

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    public void connect(String username) throws IOException {
        if (socket == null || !socket.isConnected()) {
            this.username = username;
            this.socket = new Socket("localhost", 1234);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gửi thông báo tham gia
            sendMessage(username + " has joined the chat");

            // Bắt đầu lắng nghe tin nhắn
            new Thread(this::listenForMessages).start();
        }
    }

    public void sendMessage(String message) throws IOException {
        if (socket != null && socket.isConnected() && bufferedWriter != null) {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
    }

    private void listenForMessages() {
        try {
            String messageFromServer;
            while (socket.isConnected() && (messageFromServer = bufferedReader.readLine()) != null) {
                final String message = messageFromServer;
                // Thông báo cho tất cả các listener
                for (MessageListener listener : messageListeners) {
                    listener.onMessageReceived(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public String getUsername() {
        return username;
    }
    public void reset() {
        disconnect();
        username = null;
        messageListeners.clear();
        instance = null;
    }

} 