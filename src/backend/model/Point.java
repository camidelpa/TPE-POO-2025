package backend.model;

public class Point {

    private final double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }

    public double getY() { return y; }

    // pitagoras to calculate distance between this point and another point
    // needed to know if theres a click on te circle ;)
    public double getDistanceTo(Point other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    @Override
    public String toString() {
        return String.format("{%.2f , %.2f}", x, y);
    }
}