package com.training.studyfx.util;

import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;

/**
 * ScaleManager — scales the root font-size proportionally when the scene
 * is resized, giving the entire UI a fluid / responsive feel.
 *
 * Design baseline: 1280 × 800 → 14 px root font.
 * Clamps between 10 px (very small window) and 18 px (very large).
 */
public class ScaleManager {

    private static final double BASE_WIDTH  = 1280.0;
    private static final double BASE_FONT   = 14.0;
    private static final double MIN_FONT    = 10.0;
    private static final double MAX_FONT    = 18.0;

    // Keep strong references so they are not GC-ed
    private static ChangeListener<Number> widthListener;
    private static Scene trackedScene;

    /** Attach responsive font-scaling to a scene. Call whenever a new scene is set. */
    public static void attach(Scene scene) {
        if (scene == null) return;

        // Detach from old scene if any
        if (trackedScene != null && widthListener != null) {
            trackedScene.widthProperty().removeListener(widthListener);
        }

        trackedScene = scene;
        widthListener = (obs, oldW, newW) -> applyScale(scene, newW.doubleValue());

        scene.widthProperty().addListener(widthListener);

        // Apply immediately with current width
        applyScale(scene, scene.getWidth() > 0 ? scene.getWidth() : BASE_WIDTH);
    }

    private static void applyScale(Scene scene, double sceneWidth) {
        double scale = sceneWidth / BASE_WIDTH;
        double fontSize = Math.max(MIN_FONT, Math.min(MAX_FONT, BASE_FONT * scale));
        // Set root font-size via inline style — overrides CSS .root font-size
        if (scene.getRoot() != null) {
            scene.getRoot().setStyle("-fx-font-size: " + String.format("%.2f", fontSize) + "px;");
        }
    }

    /** Re-apply scale after theme change (root style gets cleared). */
    public static void reapply() {
        if (trackedScene != null) {
            double w = trackedScene.getWidth() > 0 ? trackedScene.getWidth() : BASE_WIDTH;
            applyScale(trackedScene, w);
        }
    }
}
