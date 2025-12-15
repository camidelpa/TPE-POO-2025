package backend;

import backend.model.Figure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CanvasState {

    private final Collection<Figure> list = new ArrayList<>();

    public void addFigure(Figure figure) {
        list.add(figure);
    }

    public void deleteFigure(Figure figure) {
        list.remove(figure);
    }

    public Iterable<Figure> figures() {
        return List.copyOf(list);
    }

}
