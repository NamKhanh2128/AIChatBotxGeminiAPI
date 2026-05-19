package com.training.studyfx.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketManager {
    private static SocketManager instance;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;
    private final List<MessageListener> listeners = new ArrayList<>();

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    private SocketManager() {
    }

    public static SocketManager getInstance() {
        if (instance == null)
            instance = new SocketManager();
        return instance;
    }

    public void connect(String username) throws IOException {
        this.username = username;
        socket = new Socket("localhost", 1235);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(() -> {
            try {
                String msg;
                while ((msg = reader.readLine()) != null) {
                    final String finalMsg = msg;
                    javafx.application.Platform.runLater(() -> {
                        for (MessageListener l : new ArrayList<>(listeners)) {
                            l.onMessageReceived(finalMsg);
                        }
                    });
                }
            } catch (IOException ignored) {
            }
        }).start();
    }

    public void sendMessage(String msg) throws IOException {
        if (writer != null)
            writer.println(msg);
    }

    public void broadcast(String msg) {
        // Implemented to resolve compilation error from ClientHandler
        javafx.application.Platform.runLater(() -> {
            for (MessageListener l : new ArrayList<>(listeners)) {
                l.onMessageReceived(msg);
            }
        });
    }

    public void addMessageListener(MessageListener listener) {
        listeners.add(listener);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public String getUsername() {
        return username;
    }

    public void reset() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignored) {
        }
    }
}