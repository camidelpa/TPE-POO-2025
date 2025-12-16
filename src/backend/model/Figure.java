package backend.model;

public interface Figure {

    void draw(GraphicsContext gc);

    boolean contains(Point p);

    void move(double dx, double dy);
}
