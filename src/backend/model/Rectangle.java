package backend.model;

public class Rectangle extends Figure {

    private Point topLeft, bottomRight;
    protected String figureName = "Rectangulo";

    public Rectangle(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public String getFigureName() {
        return figureName;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    @Override
    public String toString() {
        return String.format("%s [ %s , %s ]", figureName, topLeft, bottomRight);
    }


    // --- IMPLEMENTACIÓN DE FIGURE ---


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

    @Override
    public Point getCenter() {
        // Calcula el promedio de X y el promedio de Y
        double centerX = (topLeft.getX() + bottomRight.getX()) / 2;
        double centerY = (topLeft.getY() + bottomRight.getY()) / 2;
        return new Point(centerX, centerY);
    }

    @Override
    public Figure deepCopy() {
        return new Rectangle(
                new Point(topLeft.getX(), topLeft.getY()),
                new Point(bottomRight.getX(), bottomRight.getY())
        );
    }

    @Override
    public Figure duplicate(double offsetX, double offsetY) {
        Point newTopLeft = new Point(
                topLeft.getX() + offsetX,
                topLeft.getY() + offsetY
        );
        Point newBottomRight = new Point(
                bottomRight.getX() + offsetX,
                bottomRight.getY() + offsetY
        );

        Rectangle duplicated = new Rectangle(newTopLeft, newBottomRight);
        copyStyleTo(duplicated);
        return duplicated;
    }
}