package com.training.studyfx.service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatHistoryManager {
    private static ChatHistoryManager instance;
    private static final String HISTORY_FILE = "chat_history.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ChatHistoryManager() {}

    public static synchronized ChatHistoryManager getInstance() {
        if (instance == null) {
            instance = new ChatHistoryManager();
        }
        return instance;
    }

    public void saveMessage(String message) {
        try (FileWriter fw = new FileWriter(HISTORY_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            String timestamp = LocalDateTime.now().format(formatter);
            out.println(timestamp + " | " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> loadHistory() {
        List<String> history = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                history.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }

    public void clearHistory() {
        try (PrintWriter writer = new PrintWriter(HISTORY_FILE)) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 