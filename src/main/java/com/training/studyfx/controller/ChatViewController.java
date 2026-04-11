package com.training.studyfx.controller;

import com.training.studyfx.service.ChatHistoryManager;
import com.training.studyfx.server.SocketManager;
import com.training.studyfx.model.User;
import com.training.studyfx.service.GeminiService;
import com.training.studyfx.service.UserService;
import com.training.studyfx.util.MarkdownToHtml;
import com.training.studyfx.exception.ChatException;
import com.training.studyfx.exception.ConnectionException;
import com.training.studyfx.exception.MessageException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.scene.web.WebView;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.Node;

public class ChatViewController implements SocketManager.MessageListener {
    @FXML private ScrollPane scrollPane;
    @FXML private TextField messageField;
    @FXML private TextField usernameField;
    @FXML private VBox chatContainer;
    @FXML private Text emptyStateText;
    @FXML private Button emojiButton;
    private Popup emojiPopup;
    private GridPane emojiGrid;
    private SocketManager socketManager;
    private ChatHistoryManager chatHistoryManager;
    private GeminiService geminiService;
    private User currentUser = UserService.getInstance().getCurrentUser();


    public void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(1.0);
        scrollPane.getStyleClass().add("right-aligned-scrollpane");
        chatContainer.getStylesheets().add(getClass().getResource("/styles/ui.css").toExternalForm());

        // Thêm xử lý scroll cho ScrollPane
        scrollPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY();

            double newVValue = scrollPane.getVvalue() - (deltaY / scrollPane.getHeight());
            scrollPane.setVvalue(Math.max(0, Math.min(1, newVValue)));
            event.consume();
        });

        // Thêm xử lý scroll cho chatContainer
        chatContainer.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double newVValue = scrollPane.getVvalue() - (deltaY / scrollPane.getHeight());
            scrollPane.setVvalue(Math.max(0, Math.min(1, newVValue)));
            event.consume();
        });

        initEmojiPopup();
        socketManager = SocketManager.getInstance();
        socketManager.addMessageListener(this);

        chatHistoryManager = ChatHistoryManager.getInstance();
        geminiService = new GeminiService();
        loadChatHistory();

        connectToServer();

    }

    private void loadChatHistory() {
        for (String message : chatHistoryManager.loadHistory()) {
            // Tách timestamp và nội dung tin nhắn
            String[] parts = message.split(" \\| ", 2);
            if (parts.length == 2) {
                appendToChat(parts[1]); // Chỉ hiển thị nội dung tin nhắn
            }
        }
        currentUser = UserService.getInstance().getCurrentUser();
        if(currentUser.getFullName() != null) {
            currentUser.bietdanh = currentUser.getFullName();
        }
        else currentUser.bietdanh = currentUser.getUsername();

    }

    @FXML
    private void showEmojiPicker() {
        if (emojiPopup.isShowing()) {
            emojiPopup.hide();
        } else {
            // Hiển thị popup dưới nút emoji
            emojiPopup.show(emojiButton.getScene().getWindow(),
                    emojiButton.localToScreen(emojiButton.getBoundsInLocal()).getMinX(),
                    emojiButton.localToScreen(emojiButton.getBoundsInLocal()).getMaxY());
        }
    }


    private void connectToServer() {
        try {
            if (socketManager.isConnected()) return;

            if (!socketManager.isConnected()) {
                socketManager.connect(currentUser.bietdanh);
                appendToChat("Connected to server as " + currentUser.bietdanh);
            } else {
                String oldUsername = socketManager.getUsername();
                if (!oldUsername.equals(currentUser.bietdanh)) {
                    socketManager.sendMessage(oldUsername + " has changed your username to " + currentUser.bietdanh);
                }
            }
        } catch (IOException e) {
            throw new ConnectionException("Không thể kết nối đến server: " + e.getMessage(), e);
        }
    }

    @FXML
    private void sendMessage() {
        try {
            if (!socketManager.isConnected()) {
                throw new ConnectionException("Chưa kết nối đến server. Vui lòng nhập username để tham gia chat");
            }
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                if (message.startsWith("@bot")) {
                    String botMessage = message.substring(4).trim();
                    String systemPrompt = "Bạn là chatbot assistant của nhóm 6 OOP.Hãy trả lời một cách chuyên nghiệp .";
                    String prompt = systemPrompt + botMessage;
                    appendToChat(socketManager.getUsername() + ": " + message);
                    chatHistoryManager.saveMessage(socketManager.getUsername() + ": " + message);

                    appendToChat("Bot: Đang nhập...");

                    new Thread(() -> {
                        try {
                            String botResponse = geminiService.generateResponse(prompt);
                            javafx.application.Platform.runLater(() -> {
                                removeLastMessage();
                                String botMsg = "@#$%^01naffajg: " + botResponse.replace("\n", "<br>");
                                chatHistoryManager.saveMessage(botMsg);
                                try {
                                    socketManager.sendMessage(botMsg);
                                } catch (IOException ex) {
                                    throw new MessageException("Lỗi gửi tin nhắn: " + ex.getMessage(), ex);
                                }
                            });
                        } catch (Exception e) {
                            javafx.application.Platform.runLater(() -> {
                                removeLastMessage();
                                throw new ChatException("Lỗi xử lý tin nhắn bot: " + e.getMessage(), e);
                            });
                        }
                    }).start();
                } else {
                    String fullMessage = socketManager.getUsername() + ": " + message;
                    socketManager.sendMessage(fullMessage);
                    chatHistoryManager.saveMessage(fullMessage);
                }
                messageField.clear();
            }
        } catch (IOException e) {
            throw new MessageException("Lỗi gửi tin nhắn: " + e.getMessage(), e);
        }
    }

    @Override
    public void onMessageReceived(String message) {
        appendToChat(message);
    }

    private void appendToChat(String message) {
        javafx.application.Platform.runLater(() -> {
            if (emptyStateText.isVisible()) {
                emptyStateText.setVisible(false);
            }

            Node messageNode;
            String username = socketManager.getUsername();

            if (message.startsWith("@#$%^01naffajg:")) {
                // Xử lý tin nhắn bot bằng WebView
                String prefix = "@#$%^01naffajg:";
                String content = message.substring(prefix.length()).replace("<br>", "\n");

                WebView webView = new WebView();
                webView.setPrefHeight(Region.USE_COMPUTED_SIZE);
                webView.setMaxHeight(Double.MAX_VALUE);
                webView.setMaxWidth(Double.MAX_VALUE);

                webView.setPrefWidth(800); // Giảm kích thước để phù hợp hơn
                webView.setMinWidth(0); // Cho phép co giãn

                // Thêm CSS cho WebView
                webView.setStyle("-fx-background-color: #e8f5e9; -fx-background-radius: 12px; -fx-border-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1); -fx-padding: 10px;");
                
                // Thêm padding cho WebView
                Insets padding = new Insets(10, 10, 10, 10);
                VBox.setMargin(webView, padding);

                // Enable JavaScript và xử lý scroll
                webView.getEngine().setJavaScriptEnabled(true);

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

                // Điều chỉnh chiều cao theo nội dung
                webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        Platform.runLater(() -> {
                            // Disable internal scrolling
                            webView.getEngine().executeScript("document.body.style.overflow = 'hidden';");

                            // Auto resize height
                            Object heightObj = webView.getEngine().executeScript("document.body.scrollHeight");
                            if (heightObj instanceof Number) {
                                double height = ((Number) heightObj).doubleValue();
                                webView.setPrefHeight(height + 20);
                            }

                            scrollToBottom();
                        });
                    }
                });

                webView.setOpacity(0);
                messageNode = webView;
            } else {
                // Sử dụng Label cho tin nhắn thông thường
                Label messageLabel = new Label(message);
                messageLabel.setMaxWidth(800);
                messageLabel.setWrapText(true);
                messageLabel.setOpacity(0);
                messageNode = messageLabel;
            }

            HBox messageBox = new HBox();
            messageBox.setMaxWidth(Double.MAX_VALUE);
            messageBox.setPadding(new Insets(5, 10, 5, 10));

            if (message.contains("has joined the chat")) {
                messageNode.getStyleClass().add("join-notification");
                messageBox.setAlignment(Pos.CENTER);
                messageBox.getChildren().add(messageNode);
            }
            else if (message.contains("has changed your username to")) {
                messageNode.getStyleClass().add("join-notification");
                messageBox.setAlignment(Pos.CENTER);
                messageBox.getChildren().add(messageNode);
            }
            else if (username != null && message.startsWith(username + ":")) {
                messageNode.getStyleClass().add("mess-global");
                messageBox.setAlignment(Pos.CENTER_RIGHT);
                
                // Thêm region trống bên trái để đẩy tin nhắn sang phải
                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                messageBox.getChildren().addAll(spacer, messageNode);
            }
            else if(message.startsWith("Bot:")) {
                messageNode.getStyleClass().add("bot-message");
                messageBox.setAlignment(Pos.CENTER_RIGHT);
                
                // Thêm region trống bên trái để đẩy tin nhắn sang phải
                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                messageBox.getChildren().addAll(spacer, messageNode);
            }
            else if(message.startsWith("@#$%^01naffajg:")) {
                // Tin nhắn bot từ Gemini
                messageBox.setAlignment(Pos.CENTER_LEFT);
                messageBox.getChildren().add(messageNode);
            }
            else {
                messageNode.getStyleClass().add("other-global");
                messageBox.setAlignment(Pos.CENTER_LEFT);
                messageBox.getChildren().add(messageNode);
            }
            chatContainer.getChildren().add(messageBox);

            // Apply fade transition
            applyFadeTransition(messageNode);

            // Auto-scroll to bottom
            scrollToBottom();
        });
    }

    private void applyFadeTransition(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void scrollToBottom() {
        // Đợi 50ms để đảm bảo layout xong mới scroll
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(50));
        pause.setOnFinished(e -> {
            chatContainer.layout(); // Cập nhật layout
            scrollPane.setVvalue(1.0); // Cuộn xuống cuối
        });
        pause.play();
    }

    private void initEmojiPopup() {
        emojiPopup = new Popup();
        emojiPopup.setAutoHide(true);

        emojiGrid = new GridPane();
        emojiGrid.setPadding(new Insets(10));
        emojiGrid.setHgap(5);
        emojiGrid.setVgap(5);
        emojiGrid.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-width: 1;");

        // Danh sách emoji
        String[] emojis = {
                // Biểu cảm khuôn mặt
                "😊", "😂", "😍", "🥰", "😎", "😇", "🤔", "😐", "😒", "😢",
                "😭", "😠", "😲", "🥳", "🤩", "🤯", "😴", "🤤", "😱", "🥺",
                "🥲", "🫠", "🥶", "🥵", "🥴", "🤢", "🤮", "🤧", "🥳", "🤠",
                "🤑", "🤐", "🤫", "🤭", "🧐",
                // Cử chỉ tay và cơ thể
                "👍", "👌", "👏", "🙏", "🙌", "🤝", "🤞", "🤏", "🤘", "🤙",
                "👋", "👎", "👊", "✊", "💪", "🤳", "🤗", "🤷‍♀️", "🤷‍♂️", "🤦‍♀️",
                "🤦‍♂️", "🙇‍♀️", "🙇‍♂️",
        };

        int col = 0;
        int row = 0;

        for (String emoji : emojis) {
            Button emojiBtn = new Button(emoji);
            emojiBtn.setStyle("-fx-background-color: transparent;");
            emojiBtn.setOnAction(e -> insertEmoji(emoji));

            emojiGrid.add(emojiBtn, col, row);

            col++;
            if (col > 9) { // 10 emoji mỗi hàng
                col = 0;
                row++;
            }
        }

        emojiPopup.getContent().add(emojiGrid);
    }

    // Phương thức chèn emoji vào text input
    private void insertEmoji(String emoji) {
        messageField.setText(messageField.getText() + emoji);
        emojiPopup.hide();
    }


    private void removeLastMessage() {
        int count = chatContainer.getChildren().size();
        if (count > 0) {
            chatContainer.getChildren().remove(count - 1);
        }
    }
}