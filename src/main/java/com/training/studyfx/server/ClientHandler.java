package com.training.studyfx.server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            // Khởi tạo các streams cho client với UTF-8
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            // Thêm BufferedWriter vào danh sách client của server
            Server.addClientWriter(bufferedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        try {
            while (socket.isConnected()) {
                // Đọc tin nhắn từ client
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient != null) {
                    System.out.println("Message from client: " + messageFromClient);

                    // Gửi tin nhắn đến tất cả các client
                    Server.sendMessageToAllClients(messageFromClient);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Đóng kết nối khi client ngắt kết nối
            closeConnections();
        }
    }

    private void closeConnections() {
        try {
            // Xóa client khỏi danh sách và đóng các streams
            Server.getClientWriters().remove(bufferedWriter);
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
