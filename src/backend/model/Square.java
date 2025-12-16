package backend.model;

public class Square extends Rectangle {

    public Square(Point topLeft, double size) {
        super(topLeft, new Point(topLeft.getX() + size, topLeft.getY() + size));
        this.figureName = "Cuadrado";
    }

    // Implementacion de figure ya esta hecha en el padre (Rectangle)
}
