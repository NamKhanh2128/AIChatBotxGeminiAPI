package com.training.studyfx.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.training.studyfx.App;
import com.training.studyfx.model.User;
import com.training.studyfx.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class UIController implements Initializable {
    @FXML
    private Circle avt;

    @FXML
    private AnchorPane mainContentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadUserAvatar();
        String targetTab = App.getTargetTab();
        App.clearNextTab();

        try {
            if ("setting".equalsIgnoreCase(targetTab)) {
                loadView("ProfileSettingView.fxml");
            } else {
                loadView("AboutView.fxml"); // mặc định
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAboutClick(MouseEvent event) {
        try {
            loadView("AboutView.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChatClick() {
        try {
            loadView("ChatView.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChatbotClick(MouseEvent event) {
        try {
            loadView("ChatbotView.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettingClick(MouseEvent event) {
        try {
            loadView("ProfileSettingView.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        if (mainContentArea != null) {
            mainContentArea.getChildren().clear();
            Parent view = FXMLLoader.load(getClass().getResource("/com/training/studyfx/" + fxmlFile));
            mainContentArea.getChildren().add(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        }
    }

    private void loadUserAvatar() {
        if (avt == null) return;

        User currentUser = UserService.getInstance().getCurrentUser();

        try {
            Image image;
            String imagePath = (currentUser != null && currentUser.getProfileImagePath() != null)
                    ? currentUser.getProfileImagePath()
                    : "/images/default-profile.png";

            if (imagePath.startsWith("/")) {
                image = new Image(getClass().getResourceAsStream(imagePath));
            } else {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    image = new Image(imageFile.toURI().toString());
                } else {
                    image = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
                }
            }

            avt.setFill(new ImagePattern(image));

        } catch (Exception e) {
            System.err.println("Error loading avatar image: " + e.getMessage());
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
            avt.setFill(new ImagePattern(defaultImage));
        }
    }
}