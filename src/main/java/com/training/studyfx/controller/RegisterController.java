package com.training.studyfx.controller;

import com.training.studyfx.App;
import com.training.studyfx.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    private UserService userService = UserService.getInstance();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Basic validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (username.length() < 4) {
            showError("Username must be at least 4 characters");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // Register the user
        if (userService.register(username, password, email)) {
            try {
                App.setRoot("LoginView");
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error returning to login");
            }
        } else {
            showError("Username already exists");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            App.setRoot("LoginView");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error returning to login");
        }
    }

    private void showError(String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }
}