package backend.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class Figure {

    private Color fillColor1;
    private Color fillColor2;
    private ShadowType shadowType;
    private BorderType borderType;
    private double borderWidth;
    private List<String> tags;
    private int layer;

    public Figure() {
        this.tags = new ArrayList<>();
        this.shadowType = ShadowType.NONE;
        this.borderType = BorderType.NORMAL;
        this.borderWidth = 1.0;
        this.fillColor1 = Color.YELLOW; // default colors
        this.fillColor2 = Color.RED;
        this.layer = 0; // default layer
    }

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

    protected void copyStyleTo(Figure target) {
        target.setFillColor1(this.fillColor1);
        target.setFillColor2(this.fillColor2);
        target.setShadowType(this.shadowType);
        target.setBorderType(this.borderType);
        target.setBorderWidth(this.borderWidth);
        target.setLayer(this.layer);
    }

    public int getLayer() { return layer; }
    public void setLayer(int layer) { this.layer = layer; }

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

    public abstract boolean contains(Point point);
    public abstract void move(double diffX, double diffY);
    public abstract Point getCenter();
    public abstract String getFigureName();
    public abstract Figure deepCopy();

    public abstract Figure duplicate(double offsetX, double offsetY);
    public abstract List<Figure> divide();
    public abstract void moveToCenter(double canvasWidth, double canvasHeight);
}