package backend.model;

public class Circle extends Ellipse {
    private final double radius;

    public Circle(Point centerPoint, double radius) {
        super(centerPoint, 2 * radius, 2 * radius);
        this.figureName = "Circulo";
        this.radius = radius;
    }

    public Circle(Point centerPoint, Point edgePoint) {
        this(centerPoint, Math.abs(edgePoint.getX() - centerPoint.getX()));
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return String.format("%s [Centro: %s, Diametro: %.2f]", figureName, centerPoint, sAxisX);
    }

    @Override
    public Point getCenter() { return centerPoint; }

    @Override
    public boolean contains(Point point) {
        return centerPoint.getDistanceTo(point) < radius;
    }

    @Override
    public Figure deepCopy() {
        return new Circle(new Point(centerPoint.getX(), centerPoint.getY()), radius);
    }
}