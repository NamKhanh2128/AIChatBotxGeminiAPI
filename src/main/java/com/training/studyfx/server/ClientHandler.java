package com.training.studyfx.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final SocketManager manager;

    public ClientHandler(Socket socket, SocketManager manager) {
        this.socket = socket;
        this.manager = manager;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            while ((msg = in.readLine()) != null) {
                manager.broadcast(msg);
            }
        } catch (IOException ignored) {
        }
    }
}