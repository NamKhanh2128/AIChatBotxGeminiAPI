package com.training.studyfx.util;

import javafx.scene.Scene;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class ThemeManager {
    private static final Path FILE = Paths.get(System.getProperty("user.home"), ".studyfx", "theme.properties");
    private static boolean dark = true;
    private static final Set<Scene> activeScenes = Collections.newSetFromMap(new WeakHashMap<>());

    public static boolean isDark() {
        return dark;
    }

    public static void setDark(boolean d) {
        dark = d;
        save();
        applyThemeToAll();
    }

    public static void toggle() {
        dark = !dark;
        save();
        applyThemeToAll();
    }

    public static void registerScene(Scene scene) {
        if (scene != null) {
            activeScenes.add(scene);
            applyTheme(scene);
        }
    }

    public static void applyTheme(Scene scene) {
        if (scene != null) {
            scene.getStylesheets().clear();
            String themeFile = dark ? "/styles/ui-dark.css" : "/styles/ui-light.css";
            java.net.URL resource = ThemeManager.class.getResource(themeFile);
            if (resource != null) {
                scene.getStylesheets().add(resource.toExternalForm());
            }
        }
    }

    private static void applyThemeToAll() {
        for (Scene scene : activeScenes) {
            applyTheme(scene);
        }
    }

    private static void load() {
        if (!Files.exists(FILE))
            return;
        try (InputStream in = Files.newInputStream(FILE)) {
            Properties p = new Properties();
            p.load(in);
            dark = !"light".equals(p.getProperty("theme"));
        } catch (IOException ignored) {
        }
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Properties p = new Properties();
            p.setProperty("theme", dark ? "dark" : "light");
            try (OutputStream out = Files.newOutputStream(FILE)) {
                p.store(out, "Theme");
            }
        } catch (IOException ignored) {
        }
    }

    static {
        load();
    }
}