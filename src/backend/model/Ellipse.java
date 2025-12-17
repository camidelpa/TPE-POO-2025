package backend.model;

import java.util.ArrayList;
import java.util.List;

public class Ellipse extends Figure {

    protected String figureName = "Elipse";

    protected Point centerPoint;
    protected double sMayorAxis, sMinorAxis; // Semiejes (radios)

    public Ellipse(Point centerPoint, double sMayorAxis, double sMinorAxis) {
        this.centerPoint = centerPoint;
        this.sMayorAxis = sMayorAxis;
        this.sMinorAxis = sMinorAxis;
    }

    @Override
    public String getFigureName() {
        return figureName;
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public double getsMayorAxis() {
        return sMayorAxis;
    }

    public double getsMinorAxis() {
        return sMinorAxis;
    }

    @Override
    public String toString() {
        return String.format("%s [Centro: %s, DMayor: %.2f, DMenor: %.2f]", figureName, centerPoint, sMayorAxis, sMinorAxis);
    }

    // --- IMPLEMENTACIÓN DE FIGURE ---

    @Override
    public Point getCenter() {
        return centerPoint;
    }

    @Override
    public boolean contains(Point point) {
        // Fórmula matemática para saber si un punto está dentro de una elipse:
        // (x - h)^2 / a^2 + (y - k)^2 / b^2 <= 1
        double normalizedX = Math.pow(point.getX() - centerPoint.getX(), 2) / Math.pow(sMayorAxis, 2);
        double normalizedY = Math.pow(point.getY() - centerPoint.getY(), 2) / Math.pow(sMinorAxis, 2);
        return (normalizedX + normalizedY) <= 1.0;
    }

    @Override
    public void move(double diffX, double diffY) {
        centerPoint = new Point(centerPoint.getX() + diffX, centerPoint.getY() + diffY);
    }

    @Override
    public Figure deepCopy() {
        return new Ellipse(new Point(centerPoint.getX(), centerPoint.getY()), sMayorAxis, sMinorAxis);
    }

    @Override
    public Figure duplicate(double offsetX, double offsetY) {
        Point newCenter = new Point(
                centerPoint.getX() + offsetX,
                centerPoint.getY() + offsetY
        );

        Ellipse duplicated = new Ellipse(newCenter, sMayorAxis, sMinorAxis);
        copyStyleTo(duplicated);
        return duplicated;
    }

    @Override
    public List<Figure> divide() {
        List<Figure> result = new ArrayList<>();

        double halfMayorAxis = sMayorAxis / 2;

        Point leftCenter = new Point(
                centerPoint.getX() - halfMayorAxis / 2,
                centerPoint.getY()
        );
        Ellipse left = new Ellipse(leftCenter, halfMayorAxis, sMinorAxis);
        copyStyleTo(left);

        Point rightCenter = new Point(
                centerPoint.getX() + halfMayorAxis / 2,
                centerPoint.getY()
        );
        Ellipse right = new Ellipse(rightCenter, halfMayorAxis, sMinorAxis);
        copyStyleTo(right);

        result.add(left);
        result.add(right);
        return result;
    }

    @Override
    public void moveToCenter(double canvasWidth, double canvasHeight) {
        centerPoint = new Point(canvasWidth / 2, canvasHeight / 2);
    }
}