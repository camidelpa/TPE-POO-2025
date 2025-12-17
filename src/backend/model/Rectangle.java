package backend.model;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<Figure> divide() {
        List<Figure> result = new ArrayList<>();

        double width = Math.abs(bottomRight.getX() - topLeft.getX());
        double height = Math.abs(bottomRight.getY() - topLeft.getY());
        double newWidth = width / 2;
        double newHeight = height / 2;

        Point center = getCenter();
        Point newP1 = new Point(center.getX() - newWidth / 2, center.getY() - newHeight / 2);

        // create two new rectangles or squares
        Figure f1, f2;
        if (this instanceof Square) {
            f1 = new Square(newP1, newWidth);
            f2 = new Square(newP1, newWidth);
        } else {
            Point newP2 = new Point(center.getX() + newWidth / 2, center.getY() + newHeight / 2);
            f1 = new Rectangle(newP1, newP2);
            f2 = new Rectangle(newP1, newP2);
        }
        copyStyleTo(f1);
        copyStyleTo(f2);

        result.add(f1);
        result.add(f2);
        return result;
    }

    @Override
    public void moveToCenter(double canvasWidth, double canvasHeight) {
        double width = Math.abs(bottomRight.getX() - topLeft.getX());
        double height = Math.abs(bottomRight.getY() - topLeft.getY());

        double centerX = canvasWidth / 2;
        double centerY = canvasHeight / 2;

        topLeft = new Point(centerX - width / 2, centerY - height / 2);
        bottomRight = new Point(centerX + width / 2, centerY + height / 2);
    }
}