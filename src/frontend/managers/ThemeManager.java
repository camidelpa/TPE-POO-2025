package frontend.managers;

import frontend.StatusPane;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class ThemeManager {

    private final Region rootPane;
    private final Pane topBar;
    private final Pane sideBar;
    private final StatusPane statusPane;
    private final ToggleButton themeToggle;

    // Constantes de estilo
    private static final String DARK_BG = "-fx-background-color: #2b2b2b;";
    private static final String LIGHT_BG = "-fx-background-color: white;";

    private static final String DARK_BAR = "-fx-background-color: #3c3f41; -fx-padding: 10; -fx-spacing: 10;";
    private static final String LIGHT_BAR = "-fx-background-color: #999; -fx-padding: 10; -fx-spacing: 10;";

    private static final String DARK_SIDEBAR = "-fx-background-color: #3c3f41; -fx-padding: 5; -fx-spacing: 10;";
    private static final String LIGHT_SIDEBAR = "-fx-background-color: #999; -fx-padding: 5; -fx-spacing: 10;";

    private static final String TEXT_WHITE = "-fx-text-fill: white;";
    private static final String TEXT_BLACK = "-fx-text-fill: black;";
    private static final String TEXT_STATUS_DARK = "-fx-text-fill: #f0f0f0; -fx-font-weight: bold;";

    public ThemeManager(Region rootPane, Pane topBar, Pane sideBar, StatusPane statusPane, ToggleButton themeToggle) {
        this.rootPane = rootPane;
        this.topBar = topBar;
        this.sideBar = sideBar;
        this.statusPane = statusPane;
        this.themeToggle = themeToggle;
    }

    public void setDarkMode(boolean isDark) {
        if (isDark) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
    }

    private void applyDarkTheme() {
        themeToggle.setText("☀");
        rootPane.setStyle(DARK_BG);
        topBar.setStyle(DARK_BAR);
        sideBar.setStyle(DARK_SIDEBAR);
        statusPane.setStyle(DARK_BAR); // Reutilizamos estilo de barra oscura

        updateTextColors(topBar, TEXT_WHITE, TEXT_BLACK);
        updateTextColors(sideBar, TEXT_WHITE, TEXT_BLACK);

        // Lógica específica para StatusPane en modo oscuro
        statusPane.getChildren().forEach(node -> {
            if (node instanceof Labeled) {
                node.setStyle(TEXT_STATUS_DARK);
            }
        });
    }

    private void applyLightTheme() {
        themeToggle.setText("🌙");
        rootPane.setStyle(LIGHT_BG);
        topBar.setStyle(LIGHT_BAR);
        sideBar.setStyle(LIGHT_SIDEBAR);
        statusPane.setStyle(LIGHT_BAR); // Reutilizamos estilo de barra clara

        updateTextColors(topBar, TEXT_BLACK, TEXT_BLACK);
        updateTextColors(sideBar, TEXT_BLACK, TEXT_BLACK);

        statusPane.getChildren().forEach(node -> {
            if (node instanceof Labeled) {
                node.setStyle(TEXT_BLACK);
            }
        });
    }

    // Helper para evitar repetición de bucles
    private void updateTextColors(Pane container, String labelColor, String buttonColor) {
        for (Node node : container.getChildren()) {
            // Ignorar el botón de ayuda específico
            if (node instanceof Button && "❓".equals(((Button) node).getText())) {
                continue;
            }

            if (node instanceof Label || node instanceof RadioButton || node instanceof CheckBox) {
                node.setStyle(labelColor);
            } else if (node instanceof Button || node instanceof ToggleButton) {
                node.setStyle(buttonColor);
            }
        }
    }
}