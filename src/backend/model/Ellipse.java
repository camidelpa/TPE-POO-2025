package backend.model;

public class Ellipse extends Figure {

    private Point centerPoint;
    private double sMayorAxis, sMinorAxis; // Semiejes (radios)

    public Ellipse(Point centerPoint, double sMayorAxis, double sMinorAxis) {
        this.centerPoint = centerPoint;
        this.sMayorAxis = sMayorAxis;
        this.sMinorAxis = sMinorAxis;
    }

    @Override
    public String getFigureName() {
        return "Elipse";
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
        return String.format("Elipse [Centro: %s, DMayor: %.2f, DMenor: %.2f]", centerPoint, sMayorAxis, sMinorAxis);
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
}