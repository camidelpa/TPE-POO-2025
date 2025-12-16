package backend.model;

public class Circle extends Ellipse {


    private double radius;

    public Circle(Point centerPoint, double radius) {
        super(centerPoint, 2 * radius, 2 * radius);
        this.figureName = "Circulo";
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return String.format("%s [Centro: %s, Diametro: %.2f]", figureName, centerPoint, sMayorAxis);
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
    public Figure deepCopy() {
        // Crea una copia nueva para la funcionalidad "Duplicar"
        return new Circle(new Point(centerPoint.getX(), centerPoint.getY()), radius);
    }
}