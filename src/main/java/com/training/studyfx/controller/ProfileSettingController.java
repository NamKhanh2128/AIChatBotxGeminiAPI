package com.training.studyfx.controller;

import com.training.studyfx.App;
import com.training.studyfx.model.User;
import com.training.studyfx.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class ProfileSettingController {
    @FXML
    private ImageView profileImage;
    @FXML
    private TextField nameField, emailField, statusField;
    @FXML private Button saveButton, logoutbutton, changePhotoButton, themeToggleButton;

    private User user = UserService.getInstance().getCurrentUser();

    @FXML
    public void initialize() {
        if (user != null) {
            nameField.setText(user.getFullName() != null ? user.getFullName() : user.getUsername());
            emailField.setText(user.getEmail() != null ? user.getEmail() : "");
            statusField.setText(user.getStatus() != null ? user.getStatus() : "Available");
        }
        if (themeToggleButton != null)
            themeToggleButton.setOnAction(e -> App.toggleTheme());
        saveButton.setOnAction(e -> {
            // lưu thông tin (có thể cập nhật UserService)
            if (user != null) {
                user.setFullName(nameField.getText());
                user.setEmail(emailField.getText());
                user.setStatus(statusField.getText());
            }
        });
        logoutbutton.setOnAction(e -> App.setRoot("LoginView"));
        changePhotoButton.setOnAction(e -> { /* photo picker placeholder */ });
    }

    @FXML
    private void toggleTheme() {
        App.toggleTheme();
    }
}