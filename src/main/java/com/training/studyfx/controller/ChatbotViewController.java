package com.training.studyfx.controller;

import com.training.studyfx.model.ChatMessage;
import com.training.studyfx.service.GeminiService;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class ChatbotViewController {
    @FXML
    private ScrollPane chatbotScrollPane;
    @FXML
    private VBox chatbotMessagesContainer;
    @FXML
    private TextField chatbotInput;

    private final GeminiService gemini = new GeminiService();

    @FXML
    public void initialize() {
        chatbotScrollPane.setFitToWidth(true);
        addBubble("Chào bạn! Tôi là Chatbot cá nhân của bạn. Tôi có thể giúp gì cho bạn hôm nay?", false);
    }

    @FXML
    private void handleSendMessage() {
        String text = chatbotInput.getText().trim();
        if (text.isEmpty())
            return;
        addBubble(text, true);
        chatbotInput.clear();

        Label typing = new Label("Thinking...");
        typing.setStyle("-fx-text-fill:#888; -fx-padding:10;");
        chatbotMessagesContainer.getChildren().add(typing);

        new Thread(() -> {
            String reply = gemini.generateResponse(text);
            Platform.runLater(() -> {
                chatbotMessagesContainer.getChildren().remove(typing);
                addBubble(reply, false);
            });
        }).start();
    }

    private void addBubble(String text, boolean isUser) {
        HBox row = new HBox();
        row.setPadding(new Insets(4, 12, 4, 12));

        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(450);
        bubble.setOpacity(0);
        boolean isError = !isUser && text.startsWith("Sorry, I encountered");
        bubble.getStyleClass().add(isUser ? "user-message" : (isError ? "error-message" : "bot-message"));

        if (isUser) {
            row.setAlignment(Pos.CENTER_RIGHT);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            row.getChildren().addAll(spacer, bubble);
        } else {
            row.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().add(bubble);
        }

        chatbotMessagesContainer.getChildren().add(row);
        FadeTransition ft = new FadeTransition(Duration.millis(300), bubble);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
        Platform.runLater(() -> chatbotScrollPane.setVvalue(1.0));
    }
}