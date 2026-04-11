package com.training.studyfx.controller;

import com.training.studyfx.App;
import com.training.studyfx.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private UserService userService = UserService.getInstance();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        if (userService.login(username, password)) {
            try {
                App.setRoot("UI");
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error loading main interface");
            }
        } else {
            showError("Invalid username or password");
        }
    }

    @FXML
    private void handleShowRegister() {
        try {
            App.setRoot("RegisterView");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading registration page");
        }
    }

    private void showError(String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }
}