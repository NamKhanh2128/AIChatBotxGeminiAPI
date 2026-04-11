package com.training.studyfx.controller;

import com.training.studyfx.model.ChatMessage;
import com.training.studyfx.model.ChatMessage.MessageType;
import com.training.studyfx.service.GeminiService;
import com.training.studyfx.util.MarkdownToHtml;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.Node;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.net.URL;

public class ChatbotViewController implements Initializable {

    @FXML
    private ScrollPane chatbotScrollPane;

    @FXML
    private VBox chatbotMessagesContainer;

    @FXML
    private TextField chatbotInput;

    private final GeminiService geminiService = new GeminiService();

    // Danh sách lưu trữ lịch sử trò chuyện
    private List<ChatMessage> chatHistory = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (chatbotScrollPane != null) {
            // Enable smooth scrolling
            chatbotScrollPane.setPannable(true);
            chatbotScrollPane.setFitToWidth(true); // Fit to width để nội dung chiếm hết chiều rộng
            chatbotScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            chatbotScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            
            // Đặt thanh cuộn ở bên phải
            chatbotScrollPane.getStyleClass().add("right-aligned-scrollpane");

            // Tối ưu hiệu suất
            chatbotScrollPane.setCache(true);
            chatbotScrollPane.setCacheShape(true);
            //Xử lý scroll
            chatbotScrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
                //scroll dọc
                if (event.getDeltaY() != 0){
                    double deltaY = event.getDeltaY();
                    double newVValue = chatbotScrollPane.getVvalue() - (deltaY / chatbotScrollPane.getHeight());
                    chatbotScrollPane.setVvalue(Math.max(0, Math.min(1, newVValue)));
                    event.consume();
                }
            });
        }

        if (chatbotMessagesContainer != null) {
            // Căn lề phải cho toàn bộ container
            chatbotMessagesContainer.setAlignment(Pos.CENTER_LEFT);
            
            addBotMessage("**Chào bạn!** Tôi là *Chatbot cá nhân* của bạn. Tôi có thể giúp gì cho bạn hôm nay?");
        }
        if (chatbotInput != null) {
            chatbotInput.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    handleSendMessage();
                }
            });
        }
    }

    @FXML
    private void handleSendMessage() {
        if (chatbotInput == null)
            return;
        String messageText = chatbotInput.getText().trim();
        if (!messageText.isEmpty()) {
            addUserMessage(messageText);
            chatbotInput.clear();
            processUserMessage(messageText);
        }
    }

    private void processUserMessage(String message) {
        addTypingIndicator();
        new Thread(() -> {
            try {
                String botResponse = geminiService.generateResponse(message);
                Platform.runLater(() -> {
                    removeTypingIndicator();
                    addBotMessage(botResponse);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    removeTypingIndicator();
                    addBotMessage("Sorry, I encountered an error: " + e.getMessage());
                });
            }
        }).start();
    }

    private void addUserMessage(String text) {
        ChatMessage message = new ChatMessage(text, MessageType.USER, getCurrentTime());
        chatHistory.add(message);
        addStyledMessageToContainer(message.getText(), "user-message", false);
    }

    private void addBotMessage(String text) {
        ChatMessage message = new ChatMessage(text, MessageType.BOT, getCurrentTime());
        chatHistory.add(message);
        addStyledMessageToContainer("Bot: " + message.getText(), "bot-message", true);
    }

    private void addTypingIndicator() {
        addStyledMessageToContainer("Bot: Typing...", "bot-message", false);
    }

    private void removeTypingIndicator() {
        int count = chatbotMessagesContainer.getChildren().size();
        if (count > 0) {
            Node lastNode = chatbotMessagesContainer.getChildren().get(count - 1);

            if (lastNode instanceof HBox) {
                HBox hbox = (HBox) lastNode;
                if (hbox.getChildren().size() > 0) {
                    Node child = hbox.getChildren().get(0);
                    if (child instanceof Label) {
                        Label label = (Label) child;
                        if (label.getText().contains("Typing...")) {
                            chatbotMessagesContainer.getChildren().remove(count - 1);
                            return;
                        }
                    }
                }
            } else if (lastNode instanceof Label) {
                Label lastLabel = (Label) lastNode;
                if (lastLabel.getText().contains("Typing...")) {
                    chatbotMessagesContainer.getChildren().remove(count - 1);
                    return;
                }
            }

        }
    }


    private void addStyledMessageToContainer(String messageText, String styleClass, boolean isMarkdown) {
        Node messageNode;

        if (isMarkdown && "bot-message".equals(styleClass)) {
            // Xử lý markdown cho tin nhắn bot bằng WebView
            String content = messageText;
            if (messageText.startsWith("Bot: ")) {
                content = messageText.substring(5); // Bỏ "Bot: " prefix
            }

            WebView webView = new WebView();
            webView.setPrefWidth(800);
            webView.setPrefHeight(Region.USE_COMPUTED_SIZE);
            webView.setMinHeight(50);
            webView.setMaxWidth(Double.MAX_VALUE);
            webView.getEngine().setUserStyleSheetLocation(null);
            
            // Thêm CSS cho WebView
            webView.setStyle("-fx-background-color: #e8f5e9; -fx-background-radius: 12px; -fx-border-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1); -fx-padding: 10px;");
            
            // Thêm padding cho WebView
            Insets padding = new Insets(10, 10, 10, 10);
            VBox.setMargin(webView, padding);

            // Disable WebView scrolling để cho phép parent ScrollPane handle scroll
            webView.getEngine().setJavaScriptEnabled(true);

            // Enable mouse scroll passthrough từ WebView lên ScrollPane
            webView.setOnScroll(event -> {
                // Pass scroll events to parent ScrollPane
                if (chatbotScrollPane != null) {
                    chatbotScrollPane.fireEvent(event.copyFor(chatbotScrollPane, chatbotScrollPane));
                }
                event.consume();
            });

            // Chuyển đổi markdown thành HTML
            String htmlContent = MarkdownToHtml.convertToHtml(content);
            String cssStyle = "<style>"
                    + "body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; padding: 10px; margin: 0; color: #2e7d32; width: 780px; }"
                    + "pre { background-color: #f5f5f5; padding: 10px; border-radius: 5px; overflow-x: auto; max-width: 760px; }"
                    + "code { font-family: 'Consolas', 'Monaco', monospace; color: #d32f2f; }"
                    + "img { max-width: 100%; height: auto; }"
                    + "table { border-collapse: collapse; width: 100%; max-width: 760px; }"
                    + "th, td { border: 1px solid #ddd; padding: 8px; }"
                    + "th { background-color: #f2f2f2; }"
                    + "h1, h2, h3, h4, h5, h6 { color: #1565c0; margin-top: 10px; margin-bottom: 5px; }"
                    + "strong { color: #1976d2; }"
                    + "em { color: #388e3c; }"
                    + "p { max-width: 760px; }"
                    + "</style>";
            webView.getEngine().loadContent(cssStyle + htmlContent);

            // Điều chỉnh chiều cao theo nội dung và disable internal scrolling
            webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    Platform.runLater(() -> {
                        // Disable internal scrolling
                        webView.getEngine().executeScript("document.body.style.overflow = 'hidden';");

                        // Auto resize height
                        Object heightObj = webView.getEngine().executeScript("document.body.scrollHeight");
                        if (heightObj instanceof Number) {
                            double height = ((Number) heightObj).doubleValue();
                            webView.setPrefHeight(height + 20); // Add some padding
                            webView.setMinHeight(height);
                        }

                        // Auto scroll to bottom after a short delay
                         scrollToBottom();
                    });
                }
            });

            webView.setOpacity(0); // Start invisible
            messageNode = webView;
        } else {
            // Sử dụng Label cho tin nhắn thông thường
            Label messageLabel = new Label(messageText);
            messageLabel.setFont(new Font(14));
            
            // Thêm CSS trực tiếp cho tin nhắn người dùng
            if ("user-message".equals(styleClass)) {
                messageLabel.setStyle("-fx-background-color: #1982FC; -fx-text-fill: white; -fx-padding: 10px 15px; -fx-background-radius: 18px 18px 0px 18px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");
            } else {
                messageLabel.getStyleClass().add(styleClass);
            }
            
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(500);
            messageLabel.setOpacity(0); // Start invisible
            messageNode = messageLabel;
        }

        if ("user-message".equals(styleClass)) {
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_RIGHT);
            hbox.setMaxWidth(Double.MAX_VALUE);
            hbox.setPadding(new Insets(5, 10, 5, 10));
            
            // Thêm region trống bên trái để đẩy tin nhắn sang phải
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            hbox.getChildren().addAll(spacer, messageNode);
            
            chatbotMessagesContainer.getChildren().add(hbox);
            applyFadeTransition(messageNode);
            // Auto scroll for user messages
            scrollToBottom();
        } else {
            // Đối với tin nhắn bot, đặt trong HBox để căn lề trái
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setMaxWidth(Double.MAX_VALUE);
            hbox.setPadding(new Insets(5, 10, 5, 10));
            hbox.getChildren().add(messageNode);
            chatbotMessagesContainer.getChildren().add(hbox);
            applyFadeTransition(messageNode);
            // Tự động cuộn xuống cuối cùng nếu tin nhắn không phải Markdown
            if (!isMarkdown) {
                scrollToBottom();
            }
        }
    }

    private void applyFadeTransition(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalDateTime.now().format(formatter);
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            // Multiple attempts to ensure scroll works
            chatbotScrollPane.setVvalue(1.0);
            Platform.runLater(() -> {
                chatbotScrollPane.setVvalue(1.0);
            });
        });
    }
}