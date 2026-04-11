package com.training.studyfx.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private static List<BufferedWriter> clientWriters = new ArrayList<>();  // Danh sách các client writers

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    // Thêm phương thức getter để truy xuất clientWriters
    public static List<BufferedWriter> getClientWriters() {
        return clientWriters;
    }

    public void startServer() {
        try {
            System.out.println("Server is starting...");
            while (!serverSocket.isClosed()) {
                // Chấp nhận kết nối từ client
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected: " + socket.getRemoteSocketAddress().toString());

                // Tạo ClientHandler để xử lý client
                ClientHandler clientHandler = new ClientHandler(socket);

                // Chạy ClientHandler trong một thread riêng biệt
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        } finally {
            closeServerSocket();
        }
    }

    // Đóng ServerSocket khi không còn cần thiết
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thêm BufferedWriter của client vào danh sách khi client kết nối
    public static synchronized void addClientWriter(BufferedWriter writer) {
        clientWriters.add(writer);
    }

    // Gửi tin nhắn đến tất cả các client
    public static synchronized void sendMessageToAllClients(String message) {
        for (BufferedWriter writer : clientWriters) {
            try {
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
