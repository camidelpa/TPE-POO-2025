package backend.model;

public class Square extends Rectangle {

    private final String figureName = "Cuadrado";

    public Square(Point topLeft, double size) {
        super(topLeft, new Point(topLeft.getX() + size, topLeft.getY() + size));
    }

    // Implementacion de figure ya esta hecha en el padre (Rectangle)
}
