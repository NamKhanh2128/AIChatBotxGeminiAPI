package com.training.studyfx.controller;

import com.training.studyfx.App;
import com.training.studyfx.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

public class RegisterController {
    @FXML
    private TextField usernameField, emailField;
    @FXML
    private PasswordField passwordField, confirmPasswordField;
    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        confirmPasswordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                handleRegister();
        });
    }

    @FXML
    private void handleRegister() {
        String u = usernameField.getText().trim();
        String e = emailField.getText().trim();
        String p = passwordField.getText();
        String c = confirmPasswordField.getText();
        if (u.isEmpty() || u.length() < 3) {
            showError("Username must be at least 3 characters");
            return;
        }
        if (e.isEmpty() || !e.contains("@")) {
            showError("Valid email required");
            return;
        }
        if (p.length() < 4) {
            showError("Password must be at least 4 characters");
            return;
        }
        if (!p.equals(c)) {
            showError("Passwords do not match");
            return;
        }
        if (UserService.getInstance().register(u, p, e)) {
            App.setRoot("UI");
        } else {
            showError("Username already exists");
        }
    }

    @FXML
    private void handleBackToLogin() {
        App.setRoot("LoginView");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}