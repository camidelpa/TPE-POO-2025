package backend.model;

public class Square extends Figure {

    private Point topLeft, bottomRight;

    public Square(Point topLeft, double size) {
        this.topLeft = topLeft;
        this.bottomRight = new Point(topLeft.getX() + size, topLeft.getY() + size);
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    @Override
    public String getFigureName() {
        return "Cuadrado";
    }

    @Override
    public String toString() {
        return String.format("Cuadrado [ %s , %s ]", topLeft, bottomRight);
    }

    // --- IMPLEMENTACIÓN DE FIGURE ---

    @Override
    public boolean contains(Point point) {
        // La lógica es igual a la del rectángulo
        return point.getX() > topLeft.getX() && point.getX() < bottomRight.getX() &&
                point.getY() > topLeft.getY() && point.getY() < bottomRight.getY();
    }

    @Override
    public void move(double diffX, double diffY) {
        topLeft = new Point(topLeft.getX() + diffX, topLeft.getY() + diffY);
        bottomRight = new Point(bottomRight.getX() + diffX, bottomRight.getY() + diffY);
    }

    @Override
    public Point getCenter() {
        double centerX = (topLeft.getX() + bottomRight.getX()) / 2;
        double centerY = (topLeft.getY() + bottomRight.getY()) / 2;
        return new Point(centerX, centerY);
    }

    @Override
    public Figure deepCopy() {
        // Calculamos el tamaño original para crear uno nuevo
        double size = Math.abs(bottomRight.getX() - topLeft.getX());
        return new Square(new Point(topLeft.getX(), topLeft.getY()), size);
    }
}
