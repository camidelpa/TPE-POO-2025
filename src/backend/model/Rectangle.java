package backend.model;

import java.util.ArrayList;
import java.util.List;

public class Rectangle extends Figure {

    private Point topLeft, bottomRight;
    protected String figureName = "Rectangulo";

    public Rectangle(Point p1, Point p2) {
        this.topLeft = new Point(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()));
        this.bottomRight = new Point(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()));
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
        double quarterHeight = (Math.abs(bottomRight.getY() - topLeft.getY()))/4;

        Point leftTopLeft = new Point(topLeft.getX(), topLeft.getY() + quarterHeight);
        Point leftBottomRight = new Point(topLeft.getX() + width / 2, bottomRight.getY() - quarterHeight);
        Rectangle left = new Rectangle(leftTopLeft, leftBottomRight);
        left.addTag(this.getTagsString());
        copyStyleTo(left);

        Point rightTopLeft = new Point(topLeft.getX() + width / 2, topLeft.getY() + quarterHeight);
        Point rightBottomRight = new Point(bottomRight.getX(), bottomRight.getY() - quarterHeight);
        Rectangle right = new Rectangle(rightTopLeft, rightBottomRight);
        right.addTag(this.getTagsString());
        copyStyleTo(right);

        result.add(left);
        result.add(right);
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

    public double getWidth() {
        return Math.abs(bottomRight.getX() - topLeft.getX());
    }

    public double getHeight() {
        return Math.abs(bottomRight.getY() - topLeft.getY());
    }
}