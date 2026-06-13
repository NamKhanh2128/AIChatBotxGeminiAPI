package com.training.studyfx.controller;

import com.training.studyfx.model.User;
import com.training.studyfx.server.SocketManager;
import com.training.studyfx.service.*;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;

public class ChatViewController implements SocketManager.MessageListener {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField messageField;
    @FXML
    private VBox chatContainer;
    @FXML
    private Label emptyStateText;

    private final SocketManager socket = SocketManager.getInstance();
    private final User user = UserService.getInstance().getCurrentUser();
    private String displayName;

    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        socket.addMessageListener(this);
        displayName = (user != null && user.getFullName() != null) ? user.getFullName()
                : (user != null ? user.getUsername() : "Guest");
        for (String msg : ChatHistoryManager.getInstance().loadHistory()) {
            append(msg);
        }
        connect();
    }

    private void connect() {
        new Thread(() -> {
            for (int attempt = 1; attempt <= 12; attempt++) {
                try {
                    Thread.sleep(500);
                    if (!socket.isConnected()) {
                        socket.connect(displayName);
                    }
                    final int a = attempt;
                    Platform.runLater(() -> append(displayName + " joined the chat"));
                    return; // kết nối thành công
                } catch (Exception e) {
                    if (attempt == 12) {
                        Platform.runLater(() ->
                            append("⚠️ Không thể kết nối server chat sau 6 giây."));
                    }
                }
            }
        }, "chat-connect-retry").start();
    }

    @FXML
    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (msg.isEmpty() || user == null)
            return;

        if (msg.startsWith("@bot ")) {
            String prompt = msg.substring(5);
            String full = displayName + ": " + msg;
            append(full);
            ChatHistoryManager.getInstance().saveMessage(full);
            append("Bot: thinking...");
            new Thread(() -> {
                String reply = "@#$%^01naffajg: " + new GeminiService().generateResponse(prompt);
                Platform.runLater(() -> {
                    removeLast();
                    append(reply);
                    ChatHistoryManager.getInstance().saveMessage(reply);
                });
            }).start();
        } else {
            String full = displayName + ": " + msg;
            try {
                socket.sendMessage(full);
            } catch (IOException ignored) {
            }
            append(full);
            ChatHistoryManager.getInstance().saveMessage(full);
        }
        messageField.clear();
    }

    @Override
    public void onMessageReceived(String msg) {
        Platform.runLater(() -> append(msg));
    }

    private void append(String msg) {
        Platform.runLater(() -> {
            if (chatContainer.lookup("#emptyStateText") != null)
                ((Label) chatContainer.lookup("#emptyStateText")).setVisible(false);
            HBox row = new HBox();
            row.setPadding(new Insets(4, 12, 4, 12));
            Label bubble = new Label(msg);
            bubble.setWrapText(true);
            bubble.setMaxWidth(500);
            bubble.setOpacity(0);

            if (msg.contains("joined") || msg.contains("left")) {
                bubble.getStyleClass().add("join-notification");
                row.setAlignment(Pos.CENTER);
            } else if (msg.startsWith("@#$%^01naffajg:")) {
                bubble.getStyleClass().add("bot-message");
                row.setAlignment(Pos.CENTER_LEFT);
            } else if (msg.startsWith(displayName + ":")) {
                bubble.getStyleClass().add("mess-global");
                row.setAlignment(Pos.CENTER_RIGHT);
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                row.getChildren().add(spacer);
            } else {
                bubble.getStyleClass().add("other-global");
                row.setAlignment(Pos.CENTER_LEFT);
            }
            row.getChildren().add(bubble);
            chatContainer.getChildren().add(row);
            FadeTransition ft = new FadeTransition(Duration.millis(300), bubble);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        });
    }

    private void removeLast() {
        if (!chatContainer.getChildren().isEmpty())
            chatContainer.getChildren().remove(chatContainer.getChildren().size() - 1);
    }

    @FXML
    private void showEmojiPicker() {
        /* tạm thời trống */ }
}