package com.training.studyfx.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SocketManager {
    private static SocketManager instance;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;
    private final List<MessageListener> listeners = new ArrayList<>();

    // Đọc host/port từ config.properties
    private static String SERVER_HOST = "localhost";
    private static int SERVER_PORT = 1235;

    static {
        try (InputStream input = SocketManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                String host = prop.getProperty("chat.server.host");
                if (host != null && !host.trim().isEmpty()) {
                    SERVER_HOST = host.trim();
                }
                String port = prop.getProperty("chat.server.port");
                if (port != null && !port.trim().isEmpty()) {
                    try {
                        SERVER_PORT = Integer.parseInt(port.trim());
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) {
            System.err.println("SocketManager: could not load config: " + e.getMessage());
        }
        System.out.println("Chat server target: " + SERVER_HOST + ":" + SERVER_PORT);
    }

    /** Called by auto-discovery to override the server address at runtime. */
    public static void setServerTarget(String host, int port) {
        SERVER_HOST = host;
        SERVER_PORT = port;
        System.out.println("Chat server target updated: " + SERVER_HOST + ":" + SERVER_PORT);
    }

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
        socket = new Socket(SERVER_HOST, SERVER_PORT);
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

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }

    public void reset() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignored) {
        }
    }
}