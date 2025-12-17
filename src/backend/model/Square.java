package backend.model;

public class Square extends Rectangle {

    public Square(Point topLeft, double size) {
        super(topLeft, new Point(topLeft.getX() + size, topLeft.getY() + size));
        this.figureName = "Cuadrado";
    }
    public Square(Point start, Point end) {
        this(calculateTopLeft(start, end), Math.abs(end.getX() - start.getX()));
    }

    private static Point calculateTopLeft(Point start, Point end) {
        double size = Math.abs(end.getX() - start.getX());
        double x = start.getX();
        double y = start.getY();

        if (end.getX() < start.getX()) x -= size;
        if (end.getY() < start.getY()) y -= size;

        return new Point(x, y);
    }


    public double getSize() {
        return Math.abs(getTopLeft().getX() - getBottomRight().getX());
    }
}
