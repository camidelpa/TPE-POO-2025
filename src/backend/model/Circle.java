package backend.model;

public class Circle extends Figure {

    private Point centerPoint;
    private double radius;

    public Circle(Point centerPoint, double radius) {
        this.centerPoint = centerPoint;
        this.radius = radius;
    }

    @Override
    public String getFigureName() {
        return "Círculo";
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return String.format("Círculo [Centro: %s, Radio: %.2f]", centerPoint, radius);
    }

    // --- MÉTODOS OBLIGATORIOS DE FIGURE ---

    @Override
    public Point getCenter() {
        // Para un círculo, el centro es su propiedad centerPoint
        return centerPoint;
    }

    @Override
    public boolean contains(Point point) {
        // Lógica movida al backend: distancia menor al radio
        return centerPoint.getDistanceTo(point) < radius;
    }

    @Override
    public void move(double diffX, double diffY) {
        centerPoint = new Point(centerPoint.getX() + diffX, centerPoint.getY() + diffY);
    }

    @Override
    public Figure deepCopy() {
        // Crea una copia nueva para la funcionalidad "Duplicar"
        return new Circle(new Point(centerPoint.getX(), centerPoint.getY()), radius);
    }
}