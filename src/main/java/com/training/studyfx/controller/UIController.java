package com.training.studyfx.controller;

import com.training.studyfx.App;
import com.training.studyfx.model.User;
import com.training.studyfx.service.UserService;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class UIController implements Initializable {

    // ── FXML fields ──
    @FXML private Circle avt;
    @FXML private StackPane mainContentArea;
    @FXML private Button About, Chat, Chatbot, Setting;
    @FXML private VBox sidebar;
    @FXML private VBox profileSection;
    @FXML private VBox sidebarFooterBox;
    @FXML private Button sidebarToggle;
    @FXML private Label aboutLabel;
    @FXML private Label chatLabel;
    @FXML private Label chatbotLabel;
    @FXML private Label settingLabel;
    @FXML private HBox menuLabelBox;

    // ── Sidebar animation lock — prevents overlapping animations ──
    private boolean animating = false;
    private javafx.animation.ParallelTransition currentFadeAnim;

    // ── Sidebar state ──
    private boolean sidebarCollapsed = false;
    private Timeline sidebarAnim;
    private static final double SIDEBAR_WIDE   = 250;
    private static final double SIDEBAR_NARROW = 68;

    // ── Nav state ──
    private String activeNav = "about";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAvatar();
        loadView("AboutView");
        setActive("about");

        // Ensure sidebar is always rendered on top of the main content area to prevent click blocking
        if (sidebar != null) {
            sidebar.setViewOrder(-1.0);
        }
        if (mainContentArea != null) {
            mainContentArea.setViewOrder(1.0);
        }
    }

    // ─────────────────────────────────────
    //  Sidebar collapse / expand
    // ─────────────────────────────────────

    @FXML
    public void toggleSidebar() {
        if (animating) return;   // Hard lock — only one animation chain at a time
        animating = true;

        boolean willCollapse = !sidebarCollapsed;
        sidebarCollapsed = willCollapse;
        double targetWidth = willCollapse ? SIDEBAR_NARROW : SIDEBAR_WIDE;

        if (willCollapse) {
            // ── COLLAPSE: fade out text (140ms) → shrink width (260ms) ──
            sidebarToggle.setText("▶");
            sidebar.getStyleClass().add("sidebar-collapsed");

            fadeLabelNodes(1.0, 0.0, 140, () -> {
                setLabelsManaged(false);
                animateWidth(targetWidth, 260, () -> animating = false);
            });

        } else {
            // ── EXPAND: show & reset text, grow width (260ms) → fade in text (140ms) ──
            sidebar.getStyleClass().remove("sidebar-collapsed");
            setLabelsManaged(true);
            setLabelsOpacity(0.0);

            animateWidth(targetWidth, 260, () -> {
                sidebarToggle.setText("◀ Collapse");
                fadeLabelNodes(0.0, 1.0, 140, () -> animating = false);
            });
        }
    }

    /** Animate sidebar width with easing. Calls onDone when finished. */
    private void animateWidth(double target, double millis, Runnable onDone) {
        if (sidebarAnim != null) sidebarAnim.stop();
        sidebarAnim = new Timeline(
            new KeyFrame(Duration.millis(millis),
                new KeyValue(sidebar.prefWidthProperty(), target, Interpolator.EASE_BOTH),
                new KeyValue(sidebar.minWidthProperty(),  target, Interpolator.EASE_BOTH),
                new KeyValue(sidebar.maxWidthProperty(),  target, Interpolator.EASE_BOTH)
            )
        );
        sidebarAnim.setOnFinished(e -> { if (onDone != null) onDone.run(); });
        sidebarAnim.play();
    }

    /** Get all nodes that should collapse/expand in the sidebar. */
    private java.util.List<javafx.scene.Node> getCollapsibleNodes() {
        java.util.List<javafx.scene.Node> nodes = new java.util.ArrayList<>();
        if (profileSection != null) nodes.add(profileSection);
        if (menuLabelBox != null) nodes.add(menuLabelBox);
        if (aboutLabel != null) nodes.add(aboutLabel);
        if (chatLabel != null) nodes.add(chatLabel);
        if (chatbotLabel != null) nodes.add(chatbotLabel);
        if (settingLabel != null) nodes.add(settingLabel);
        if (sidebarFooterBox != null) nodes.add(sidebarFooterBox);
        return nodes;
    }

    /**
     * Fade all collapsible sidebar label nodes simultaneously.
     * Calls onDone after the animation completes.
     * Stops any currently running fade before starting a new one.
     */
    private void fadeLabelNodes(double from, double to, double millis, Runnable onDone) {
        if (currentFadeAnim != null) currentFadeAnim.stop();

        java.util.List<javafx.scene.Node> nodes = getCollapsibleNodes();
        if (nodes.isEmpty()) {
            if (onDone != null) onDone.run();
            return;
        }

        currentFadeAnim = new javafx.animation.ParallelTransition();
        for (javafx.scene.Node n : nodes) {
            FadeTransition ft = new FadeTransition(Duration.millis(millis), n);
            ft.setFromValue(from);
            ft.setToValue(to);
            currentFadeAnim.getChildren().add(ft);
        }
        currentFadeAnim.setOnFinished(e -> { if (onDone != null) onDone.run(); });
        currentFadeAnim.play();
    }

    private void setLabelsManaged(boolean managed) {
        for (javafx.scene.Node n : getCollapsibleNodes()) {
            n.setVisible(managed);
            n.setManaged(managed);
        }
    }

    private void setLabelsOpacity(double opacity) {
        for (javafx.scene.Node n : getCollapsibleNodes()) {
            n.setOpacity(opacity);
        }
    }


    // ─────────────────────────────────────
    //  Navigation handlers
    // ─────────────────────────────────────

    @FXML
    private void handleAboutClick() {
        System.out.println("handleAboutClick fired. Collapsed = " + sidebarCollapsed + ", animating = " + animating);
        loadView("AboutView");
        setActive("about");
    }

    @FXML
    private void handleChatClick() {
        System.out.println("handleChatClick fired. Collapsed = " + sidebarCollapsed + ", animating = " + animating);
        loadView("ChatView");
        setActive("chat");
    }

    @FXML
    private void handleChatbotClick() {
        System.out.println("handleChatbotClick fired. Collapsed = " + sidebarCollapsed + ", animating = " + animating);
        loadView("ChatbotView");
        setActive("chatbot");
    }

    @FXML
    private void handleSettingClick() {
        System.out.println("handleSettingClick fired. Collapsed = " + sidebarCollapsed + ", animating = " + animating);
        loadView("ProfileSettingView");
        setActive("settings");
    }

    @FXML
    public void toggleTheme() {
        App.toggleTheme();
    }

    // ─────────────────────────────────────
    //  View loading (with fade transition)
    // ─────────────────────────────────────

    private void loadView(String name) {
        if (mainContentArea == null) return;
        try {
            Parent view = FXMLLoader.load(
                getClass().getResource("/com/training/studyfx/" + name + ".fxml"));
            view.setOpacity(0);
            mainContentArea.getChildren().setAll(view);
            FadeTransition ft = new FadeTransition(Duration.millis(220), view);
            ft.setToValue(1);
            ft.play();
        } catch (Exception e) {
            System.err.println("Load view failed: " + name + " – " + e.getMessage());
        }
    }

    // ─────────────────────────────────────
    //  Active nav highlight
    // ─────────────────────────────────────

    private void setActive(String nav) {
        activeNav = nav;
        About.getStyleClass().remove("active");
        Chat.getStyleClass().remove("active");
        Chatbot.getStyleClass().remove("active");
        Setting.getStyleClass().remove("active");
        switch (nav) {
            case "about"    -> About.getStyleClass().add("active");
            case "chat"     -> Chat.getStyleClass().add("active");
            case "chatbot"  -> Chatbot.getStyleClass().add("active");
            case "settings" -> Setting.getStyleClass().add("active");
        }
    }

    // ─────────────────────────────────────
    //  Avatar loader
    // ─────────────────────────────────────

    private void loadAvatar() {
        if (avt == null) return;
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
        } catch (Exception ignored) {}
    }
}
