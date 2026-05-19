package com.training.studyfx.controller;

import com.training.studyfx.App;
import com.training.studyfx.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();
        if (u.isEmpty() || p.isEmpty()) {
            showError("Please fill all fields");
            return;
        }
        if (UserService.getInstance().login(u, p)) {
            App.setRoot("UI");
        } else {
            showError("Invalid username or password");
        }
    }

    @FXML
    private void handleShowRegister() {
        App.setRoot("RegisterView");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}