package com.training.studyfx;

import com.training.studyfx.util.ThemeManager;
import com.training.studyfx.util.ScaleManager;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;

public class App extends Application {

    private static Scene scene;
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("StudyFX Chat");
        stage.setMinWidth(420);
        stage.setMinHeight(560);
        setIcon();

        // ===== QUAN TRỌNG: KHÔNG khởi động server trước khi hiển thị UI =====
        // Server sẽ được khởi động SAU KHI UI đã hiển thị

        try {
            // Load LoginView TRƯỚC
            URL fxmlUrl = App.class.getResource("LoginView.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = App.class.getResource("/com/training/studyfx/LoginView.fxml");
            }

            if (fxmlUrl == null) {
                System.err.println("FATAL: Cannot find LoginView.fxml");
                Platform.exit();
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            scene = new Scene(root, 420, 560);

            ThemeManager.registerScene(scene);
            ScaleManager.attach(scene);

            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            // ===== KHỞI ĐỘNG SERVER SAU KHI UI ĐÃ HIỂN THỊ =====
            // Dùng Platform.runLater để đảm bảo UI đã render xong
            Platform.runLater(() -> {
                startServerInBackground();
            });

            System.out.println("UI loaded successfully!");

        } catch (Exception e) {
            System.err.println("FATAL: Failed to start application");
            e.printStackTrace();
            Platform.exit();
        }

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * KHÔNG BAO GIỜ gọi trực tiếp - luôn dùng Thread riêng
     */
    private void startServerInBackground() {
        Thread serverThread = new Thread(() -> {
            try {
                // Delay nhẹ để UI render xong
                Thread.sleep(500);

                com.training.studyfx.server.Server server = new com.training.studyfx.server.Server(1235

                );
                server.startServer(); // Blocking call - OK vì đang ở background thread

            } catch (Exception e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }, "chat-server");
        serverThread.setDaemon(true);
        serverThread.start();

        System.out.println("Server starting in background...");
    }

    // ===== NAVIGATION =====

    public static void setRoot(String fxml) {
        try {
            URL url = App.class.getResource(fxml + ".fxml");
            if (url == null) {
                url = App.class.getResource("/com/training/studyfx/" + fxml + ".fxml");
            }

            if (url == null) {
                System.err.println("FXML not found: " + fxml);
                return;
            }

            Parent root = FXMLLoader.load(url);
            Parent old = scene.getRoot();

            if (old != null) {
                FadeTransition out = new FadeTransition(Duration.millis(120), old);
                out.setFromValue(1);
                out.setToValue(0);
                out.setOnFinished(e -> {
                    scene.setRoot(root);
                    adjustWindow(fxml);
                    ThemeManager.registerScene(scene);
                    ScaleManager.reapply();
                    root.setOpacity(0);
                    FadeTransition in = new FadeTransition(Duration.millis(200), root);
                    in.setFromValue(0);
                    in.setToValue(1);
                    in.play();
                });
                out.play();
            } else {
                scene.setRoot(root);
                adjustWindow(fxml);
                ThemeManager.registerScene(scene);
                ScaleManager.reapply();
            }
        } catch (Exception e) {
            System.err.println("Navigation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadView(String fxml, double w, double h) {
        try {
            URL url = App.class.getResource(fxml + ".fxml");
            if (url == null) {
                url = App.class.getResource("/com/training/studyfx/" + fxml + ".fxml");
            }

            if (url == null) {
                System.err.println("FXML not found: " + fxml);
                return;
            }

            Parent root = FXMLLoader.load(url);
            if (scene == null) {
                scene = new Scene(root, w, h);
            } else {
                scene.setRoot(root);
            }
            adjustWindow(fxml);
            ThemeManager.registerScene(scene);
            stage.setWidth(w);
            stage.setHeight(h);
            centerOnScreen();
        } catch (Exception e) {
            System.err.println("Load view failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void adjustWindow(String fxml) {
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double sw = bounds.getWidth();
        double sh = bounds.getHeight();

        switch (fxml) {
            case "UI": {
                // Use 88% of screen, capped at 1400×900
                double w = Math.min(sw * 0.88, 1400);
                double h = Math.min(sh * 0.88, 900);
                double minW = Math.min(sw * 0.6, 960);
                double minH = Math.min(sh * 0.6, 640);
                stage.setMinWidth(minW);
                stage.setMinHeight(minH);
                stage.setWidth(w);
                stage.setHeight(h);
                // Maximize if screen is small (< 1366 wide)
                if (sw < 1366) {
                    stage.setMaximized(true);
                }
                break;
            }
            case "LoginView":
            case "RegisterView": {
                double w = Math.min(sw * 0.9, 420);
                double h = Math.min(sh * 0.9, 620);
                stage.setMinWidth(380);
                stage.setMinHeight(520);
                stage.setWidth(w);
                stage.setHeight(h);
                stage.setMaximized(false);
                break;
            }
        }
        centerOnScreen();
    }

    private static void centerOnScreen() {
        Screen screen = Screen.getPrimary();
        stage.setX((screen.getVisualBounds().getWidth() - stage.getWidth()) / 2);
        stage.setY((screen.getVisualBounds().getHeight() - stage.getHeight()) / 2);
    }

    // ===== THEME =====

    public static void applyTheme() {
        ThemeManager.applyTheme(scene);
    }

    public static void toggleTheme() {
        ThemeManager.toggle();
    }

    public static boolean isDark() {
        return ThemeManager.isDark();
    }

    private void setIcon() {
        try (InputStream is = App.class.getResourceAsStream("/images/logo.png")) {
            if (is != null)
                stage.getIcons().add(new Image(is));
        } catch (Exception ignored) {
        }
    }

    // ===== GETTERS =====

    public static Scene getScene() {
        return scene;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}