package backend.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class Figure {

    // --- ESTADO COMPARTIDO ---
    private Color fillColor1;
    private Color fillColor2;
    private ShadowType shadowType;
    private BorderType borderType;
    private double borderWidth;
    private List<String> tags;

    // Constructor base
    public Figure() {
        this.tags = new ArrayList<>();
        this.shadowType = ShadowType.NONE;
        this.borderType = BorderType.NORMAL;
        this.borderWidth = 1.0;
        this.fillColor1 = Color.YELLOW; // Colores por defecto [cite: 226]
        this.fillColor2 = Color.RED;
    }

    // --- MÉTODOS COMPARTIDOS ---
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public void replaceTags(List<String> newTags) {
        this.tags = new ArrayList<>(newTags);
    }

    public String getTagsString() {
        return String.join(" ", tags);
    }

    // --- GETTERS Y SETTERS ---
    public Color getFillColor1() { return fillColor1; }
    public void setFillColor1(Color fillColor1) { this.fillColor1 = fillColor1; }

    public Color getFillColor2() { return fillColor2; }
    public void setFillColor2(Color fillColor2) { this.fillColor2 = fillColor2; }

    public ShadowType getShadowType() { return shadowType; }
    public void setShadowType(ShadowType shadowType) { this.shadowType = shadowType; }

    public BorderType getBorderType() { return borderType; }
    public void setBorderType(BorderType borderType) { this.borderType = borderType; }

    public double getBorderWidth() { return borderWidth; }
    public void setBorderWidth(double borderWidth) { this.borderWidth = borderWidth; }

    // --- MÉTODOS ABSTRACTOS ---
    public abstract void draw(GraphicsContext gc);
    public abstract boolean contains(Point point);
    public abstract void move(double diffX, double diffY);
    public abstract Point getCenter(); // Necesario para "Mover al Centro" [cite: 248]
    public abstract String getFigureName();
    public abstract Figure deepCopy(); // Necesario para "Duplicar" [cite: 231]
}