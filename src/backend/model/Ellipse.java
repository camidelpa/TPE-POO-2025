package backend.model;

import java.util.ArrayList;
import java.util.List;

public class Ellipse extends Figure {

    protected String figureName = "Elipse";
    protected Point centerPoint;
    protected double sAxisX, sAxisY;

    public Ellipse(Point centerPoint, double sAxisX, double sAxisY) {
        this.centerPoint = centerPoint;
        this.sAxisX = sAxisX;
        this.sAxisY = sAxisY;
    }

    public Ellipse(Point startPoint, Point endPoint) {
        this(
                new Point((startPoint.getX() + endPoint.getX()) / 2,
                        (startPoint.getY() + endPoint.getY()) / 2),
                Math.abs(endPoint.getX() - startPoint.getX()),
                Math.abs(endPoint.getY() - startPoint.getY())
        );
    }

    @Override
    public String getFigureName() { return figureName; }

    public Point getCenterPoint() { return centerPoint; }

    public double getsAxisX() { return sAxisX; }

    public double getsAxisY() { return sAxisY; }

    @Override
    public Point getCenter() { return centerPoint;}

    @Override
    public String toString() {
        return String.format("%s [Centro: %s, Eje X: %.2f, Eje Y: %.2f]", figureName, centerPoint, sAxisX, sAxisY);
    }

    @Override
    public boolean contains(Point point) {
        double normalizedX = Math.pow(point.getX() - centerPoint.getX(), 2) / Math.pow(sAxisX, 2);
        double normalizedY = Math.pow(point.getY() - centerPoint.getY(), 2) / Math.pow(sAxisY, 2);
        return (normalizedX + normalizedY) <= 1.0;
    }

    @Override
    public void move(double diffX, double diffY) {
        centerPoint = new Point(centerPoint.getX() + diffX, centerPoint.getY() + diffY);
    }

    @Override
    public Figure deepCopy() {
        return new Ellipse(new Point(centerPoint.getX(), centerPoint.getY()), sAxisX, sAxisY);
    }

    @Override
    public Figure duplicate(double offsetX, double offsetY) {
        Point newCenter = new Point(
                centerPoint.getX() + offsetX,
                centerPoint.getY() + offsetY
        );

        Ellipse duplicated = new Ellipse(newCenter, sAxisX, sAxisY);
        copyStyleTo(duplicated);
        return duplicated;
    }

    @Override
    public List<Figure> divide() {
        List<Figure> result = new ArrayList<>();

        double newAxisX = sAxisX / 2;
        double newAxisY = sAxisY / 2;

        Ellipse e1 = new Ellipse(centerPoint, newAxisX, newAxisY);
        copyStyleTo(e1);

        Ellipse e2 = new Ellipse(centerPoint, newAxisX, newAxisY);
        copyStyleTo(e2);

        result.add(e1);
        result.add(e2);
        return result;
    }

    @Override
    public void moveToCenter(double canvasWidth, double canvasHeight) {
        centerPoint = new Point(canvasWidth / 2, canvasHeight / 2);
    }
}