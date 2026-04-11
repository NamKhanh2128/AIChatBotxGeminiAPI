package com.training.studyfx.controller;

import com.training.studyfx.App;
import com.training.studyfx.server.SocketManager;
import com.training.studyfx.model.User;
import com.training.studyfx.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileSettingController implements Initializable {
    public String profileImagePath = "/images/default-profile.png";

    @FXML
    private ImageView profileImage;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField statusField;

    @FXML
    private Label saveConfirmationLabel;

    @FXML
    private Button changePhotoButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button logoutbutton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check if UI components are properly loaded
        if (nameField == null || emailField == null || statusField == null) {
            System.err.println("Error: UI fields are null in ProfileViewController");
            return;
        }
        if (logoutbutton != null) {
            logoutbutton.setOnAction(e -> handleLogout());
        }

        // Load user profile data
        loadUserProfile();

        if (changePhotoButton != null) {
            changePhotoButton.setOnAction(e -> changeProfilePhoto());
        }

        if (saveButton != null) {
            saveButton.setOnAction(e -> saveProfile());
        }
    }

    private void loadUserProfile() {
        UserService userService = UserService.getInstance();
        User currentUser = userService.getCurrentUser();

        if (currentUser != null) {
            // Load user data
            nameField.setText(currentUser.getFullName() != null ?
                    currentUser.getFullName() : "Default Name");

            //currentUser.bietdanh = nameField.getText();


            emailField.setText(currentUser.getEmail() != null ?
                    currentUser.getEmail() : "");
            statusField.setText(currentUser.getStatus() != null ?
                    currentUser.getStatus() : "Available");

            // Load profile image if available
            if (currentUser.getProfileImagePath() != null && !currentUser.getProfileImagePath().isEmpty()) {
                try {
                    profileImagePath = currentUser.getProfileImagePath();
                    Image image;

                    // Check if it's a resource path or file path
                    if (profileImagePath.startsWith("/")) {
                        // Resource path
                        image = new Image(getClass().getResourceAsStream(profileImagePath));
                    } else {
                        // File path
                        File imageFile = new File(profileImagePath);
                        if (imageFile.exists()) {
                            image = new Image(imageFile.toURI().toString());
                        } else {
                            // Fallback to default
                            image = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
                        }
                    }

                    profileImage.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error loading profile image: " + e.getMessage());
                    // Set default image
                    profileImage.setImage(new Image(getClass().getResourceAsStream("/images/default-profile.png")));
                }
            } else {
                // Set default image
                profileImagePath = "/images/default-profile.png";
                profileImage.setImage(new Image(getClass().getResourceAsStream(profileImagePath)));
            }
        } else {
            // Default values if no user is logged in
            nameField.setText("");
            emailField.setText("");
            statusField.setText("Available");
            profileImagePath = "/images/default-profile.png";
            profileImage.setImage(new Image(getClass().getResourceAsStream(profileImagePath)));
        }
    }

    private void changeProfilePhoto() {
        if (profileImage == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(profileImage.getScene().getWindow());
        if (selectedFile != null) {
            try {
                File appDir = new File("upload");
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }

                // Create images subdirectory if it doesn't exist
                File imagesDir = new File(appDir, "images");
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }

                // Generate a unique filename based on username and timestamp
                String username = UserService.getInstance().getCurrentUser().getUsername();
                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.'));
                String newFileName = username + "_" + System.currentTimeMillis() + extension;
                File destFile = new File(imagesDir, newFileName);

                // Copy the file
                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                // Set the image in UI
                Image image = new Image(destFile.toURI().toString());
                profileImage.setImage(image);

                // Save path to database (store as absolute path)
                profileImagePath = destFile.getAbsolutePath();
                UserService.getInstance().updateProfileImage(profileImagePath);

                saveConfirmationLabel.setText("Profile image updated successfully!");
                saveConfirmationLabel.setVisible(true);

            } catch (Exception e) {
                saveConfirmationLabel.setText("Error saving image: " + e.getMessage());
                saveConfirmationLabel.setVisible(true);
                e.printStackTrace();
            }
        }
    }


    private void saveProfile() {
        String name = nameField.getText().trim();
        String status = statusField.getText().trim();
        UserService userService = UserService.getInstance();
        userService.updateUserProfile(name, status, profileImagePath);
        saveConfirmationLabel.setVisible(true);

        try {
            App.setTargetTab("setting");
            App.setRoot("UI");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void handleLogout() {
        try {
            if (SocketManager.getInstance().isConnected()) {
                SocketManager.getInstance().sendMessage(SocketManager.getInstance().getUsername() + " has left the chat");
            }
            SocketManager.getInstance().reset();
            App.setRoot("LoginView");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
