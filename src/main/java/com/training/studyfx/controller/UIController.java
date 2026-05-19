package com.training.studyfx.controller;

import com.training.studyfx.App;
import com.training.studyfx.model.User;
import com.training.studyfx.service.UserService;
import javafx.animation.FadeTransition;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class UIController implements Initializable {
    @FXML
    private Circle avt;
    @FXML
    private StackPane mainContentArea;
    @FXML
    private Button About, Chat, Chatbot, Setting, themeToggleBtn;

    private String activeNav = "about";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAvatar();
        loadView("AboutView");
        setActive("about");
        if (themeToggleBtn != null)
            themeToggleBtn.setOnMouseClicked(e -> toggleTheme());
    }

    private void loadAvatar() {
        if (avt == null)
            return;
        User u = UserService.getInstance().getCurrentUser();
        try {
            Image img;
            String path = u != null ? u.getProfileImagePath() : null;
            if (path != null && !path.isEmpty() && new File(path).exists()) {
                img = new Image(new File(path).toURI().toString());
            } else {
                img = new Image(getClass().getResourceAsStream("/images/default_profile.png"));
            }
            if (img != null && !img.isError())
                avt.setFill(new ImagePattern(img));
        } catch (Exception ignored) {
        }
    }

    @FXML
    private void handleAboutClick() {
        loadView("AboutView");
        setActive("about");
    }

    @FXML
    private void handleChatClick() {
        loadView("ChatView");
        setActive("chat");
    }

    @FXML
    private void handleChatbotClick() {
        loadView("ChatbotView");
        setActive("chatbot");
    }

    @FXML
    private void handleSettingClick() {
        loadView("ProfileSettingView");
        setActive("settings");
    }

    @FXML
    private void toggleTheme() {
        App.toggleTheme();
    }

    private void loadView(String name) {
        if (mainContentArea == null)
            return;
        try {
            mainContentArea.getChildren().clear();
            Parent view = FXMLLoader.load(getClass().getResource("/com/training/studyfx/" + name + ".fxml"));
            view.setOpacity(0);
            mainContentArea.getChildren().add(view);
            FadeTransition ft = new FadeTransition(Duration.millis(200), view);
            ft.setToValue(1);
            ft.play();
        } catch (Exception e) {
            System.err.println("Load view failed: " + name + " - " + e.getMessage());
        }
    }

    private void setActive(String nav) {
        activeNav = nav;
        About.getStyleClass().remove("active");
        Chat.getStyleClass().remove("active");
        Chatbot.getStyleClass().remove("active");
        Setting.getStyleClass().remove("active");
        switch (nav) {
            case "about" -> About.getStyleClass().add("active");
            case "chat" -> Chat.getStyleClass().add("active");
            case "chatbot" -> Chatbot.getStyleClass().add("active");
            case "settings" -> Setting.getStyleClass().add("active");
        }
    }
}