package com.training.studyfx.server;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.*;

public class Server {

    private ServerSocket serverSocket;
    private static final Set<PrintWriter> clients = ConcurrentHashMap.newKeySet();
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void startServer() {
        System.out.println("Server listening on port " + serverSocket.getLocalPort());
        while (!serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                pool.submit(() -> handleClient(client));
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    System.err.println("Accept failed: " + e.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket socket) {
        try (socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            clients.add(out);
            System.out.println("Client connected. Total: " + clients.size());

            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("Received: " + msg);
                for (PrintWriter c : clients) {
                    if (c != out)
                        c.println(msg);
                }
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } finally {
            clients.removeIf(pw -> pw.checkError());
            System.out.println("Client removed. Total: " + clients.size());
        }
    }

    public static Set<PrintWriter> getClients() {
        return clients;
    }
}