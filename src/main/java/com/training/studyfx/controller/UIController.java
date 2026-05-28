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
import javafx.scene.image.Image;
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
    }

    // ─────────────────────────────────────
    //  Sidebar collapse / expand
    // ─────────────────────────────────────

    @FXML
    public void toggleSidebar() {
        if (sidebarAnim != null && sidebarAnim.getStatus() == javafx.animation.Animation.Status.RUNNING) return;

        boolean willCollapse = !sidebarCollapsed;
        double targetWidth = willCollapse ? SIDEBAR_NARROW : SIDEBAR_WIDE;

        if (willCollapse) {
            // COLLAPSE: fade out labels first, then shrink
            FadeTransition fadeOut = buildLabelFade(1.0, 0.0, 160);
            fadeOut.setOnFinished(e -> {
                setLabelsManaged(false);
                sidebarToggle.setText("▶");
                sidebar.getStyleClass().add("sidebar-collapsed");

                sidebarAnim = buildWidthAnim(targetWidth, 270);
                sidebarAnim.play();
            });
            fadeOut.play();
        } else {
            // EXPAND: grow width first, then fade in labels
            sidebar.getStyleClass().remove("sidebar-collapsed");
            setLabelsManaged(true);
            setLabelsOpacity(0.0);

            sidebarAnim = buildWidthAnim(targetWidth, 270);
            sidebarAnim.setOnFinished(e -> {
                sidebarToggle.setText("◀ Collapse");
                FadeTransition fadeIn = buildLabelFade(0.0, 1.0, 180);
                fadeIn.play();
            });
            sidebarAnim.play();
        }

        sidebarCollapsed = willCollapse;
    }

    /** Build a width-animation Timeline */
    private Timeline buildWidthAnim(double target, double millis) {
        return new Timeline(
            new KeyFrame(Duration.millis(millis),
                new KeyValue(sidebar.prefWidthProperty(), target, Interpolator.EASE_BOTH),
                new KeyValue(sidebar.minWidthProperty(),  target, Interpolator.EASE_BOTH),
                new KeyValue(sidebar.maxWidthProperty(),  target, Interpolator.EASE_BOTH)
            )
        );
    }

    /** Build a FadeTransition that runs on all collapsible label containers */
    private FadeTransition buildLabelFade(double from, double to, double millis) {
        // We fade the profileSection as a proxy (it contains the main visual block)
        // Nav labels opacity is handled by a parallel Timeline on the sidebar node
        javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition();

        if (profileSection != null) {
            FadeTransition ft = new FadeTransition(Duration.millis(millis), profileSection);
            ft.setFromValue(from); ft.setToValue(to);
            pt.getChildren().add(ft);
        }

        sidebar.lookupAll(".nav-label").forEach(n -> {
            FadeTransition ft = new FadeTransition(Duration.millis(millis), n);
            ft.setFromValue(from); ft.setToValue(to);
            pt.getChildren().add(ft);
        });

        sidebar.lookupAll(".sidebar-menu-label").forEach(n -> {
            FadeTransition ft = new FadeTransition(Duration.millis(millis), n);
            ft.setFromValue(from); ft.setToValue(to);
            pt.getChildren().add(ft);
        });

        if (sidebarFooterBox != null) {
            FadeTransition ft = new FadeTransition(Duration.millis(millis), sidebarFooterBox);
            ft.setFromValue(from); ft.setToValue(to);
            pt.getChildren().add(ft);
        }

        // Wrap in a FadeTransition-compatible callback using a dummy node + pt trick:
        // Return a plain FadeTransition on sidebar (opacity irrelevant) but run pt inline
        FadeTransition dummy = new FadeTransition(Duration.millis(millis), sidebar);
        dummy.setFromValue(sidebar.getOpacity());
        dummy.setToValue(sidebar.getOpacity());
        dummy.setOnFinished(e -> {}); // will be overridden by caller if needed

        // Run the real fade via ParallelTransition — store reference so caller can chain
        pt.play();

        // We return dummy so caller can setOnFinished(); pt runs concurrently
        return dummy;
    }

    private void setLabelsManaged(boolean managed) {
        if (profileSection != null) {
            profileSection.setVisible(managed);
            profileSection.setManaged(managed);
        }
        sidebar.lookupAll(".nav-label").forEach(n -> {
            n.setVisible(managed);
            n.setManaged(managed);
        });
        sidebar.lookupAll(".sidebar-menu-label").forEach(n -> {
            n.setVisible(managed);
            n.setManaged(managed);
        });
        if (sidebarFooterBox != null) {
            sidebarFooterBox.setVisible(managed);
            sidebarFooterBox.setManaged(managed);
        }
    }

    private void setLabelsOpacity(double opacity) {
        if (profileSection != null) profileSection.setOpacity(opacity);
        sidebar.lookupAll(".nav-label").forEach(n -> n.setOpacity(opacity));
        sidebar.lookupAll(".sidebar-menu-label").forEach(n -> n.setOpacity(opacity));
        if (sidebarFooterBox != null) sidebarFooterBox.setOpacity(opacity);
    }


    // ─────────────────────────────────────
    //  Navigation handlers
    // ─────────────────────────────────────

    @FXML private void handleAboutClick()   { loadView("AboutView");         setActive("about");    }
    @FXML private void handleChatClick()    { loadView("ChatView");          setActive("chat");     }
    @FXML private void handleChatbotClick() { loadView("ChatbotView");       setActive("chatbot");  }
    @FXML private void handleSettingClick() { loadView("ProfileSettingView"); setActive("settings"); }

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
