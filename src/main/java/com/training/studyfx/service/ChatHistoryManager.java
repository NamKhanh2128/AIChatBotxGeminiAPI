package com.training.studyfx.service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatHistoryManager {

    private static ChatHistoryManager instance;
    private static final Path FILE = Paths.get("chat_history.txt");
    private static final int MAX_LINES = 2000;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ChatHistoryManager() {
    }

    public static ChatHistoryManager getInstance() {
        if (instance == null)
            instance = new ChatHistoryManager();
        return instance;
    }

    public synchronized void saveMessage(String message) {
        try {
            String line = "[" + LocalDateTime.now().format(df) + "] " + message;
            Files.writeString(FILE, line + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            trim();
        } catch (IOException ignored) {
        }
    }

    public List<String> loadHistory() {
        List<String> list = new ArrayList<>();
        if (!Files.exists(FILE))
            return list;
        try {
            List<String> lines = Files.readAllLines(FILE);
            int start = Math.max(0, lines.size() - 200);
            for (int i = start; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    int bracketEnd = line.indexOf("] ");
                    list.add(bracketEnd > 0 ? line.substring(bracketEnd + 2) : line);
                }
            }
        } catch (IOException ignored) {
        }
        return list;
    }

    private void trim() {
        try {
            List<String> lines = Files.readAllLines(FILE);
            if (lines.size() > MAX_LINES) {
                Files.write(FILE, lines.subList(lines.size() - MAX_LINES, lines.size()));
            }
        } catch (IOException ignored) {
        }
    }

    public void clear() {
        try {
            Files.deleteIfExists(FILE);
        } catch (IOException ignored) {
        }
    }
}