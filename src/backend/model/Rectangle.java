package backend.model;

public class Rectangle extends Figure {

    private Point topLeft, bottomRight;

    public Rectangle(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public String getFigureName() {
        return "Rectángulo";
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    @Override
    public String toString() {
        return String.format("Rectángulo [ %s , %s ]", topLeft, bottomRight);
    }

    // IMPLEMENTACIÓN NUEVA: Lógica matemática movida al backend
    @Override
    public boolean contains(Point point) {
        return point.getX() > topLeft.getX() && point.getX() < bottomRight.getX() &&
                point.getY() > topLeft.getY() && point.getY() < bottomRight.getY();
    }

    @Override
    public void move(double diffX, double diffY) {
        topLeft = new Point(topLeft.getX() + diffX, topLeft.getY() + diffY);
        bottomRight = new Point(bottomRight.getX() + diffX, bottomRight.getY() + diffY);
    }
}
