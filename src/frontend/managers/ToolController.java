package frontend.managers;

import backend.model.*;
import javafx.scene.control.ToggleButton;
import java.util.Map;
import java.util.function.BiFunction;

public class ToolController {

    private Point startPoint;
    private Figure selectedFigure;
    private Figure previewFigure;

    private final Map<ToggleButton, BiFunction<Point, Point, Figure>> strategies;

    public ToolController(Map<ToggleButton, BiFunction<Point, Point, Figure>> strategies) {
        this.strategies = strategies;
    }

    public void onMousePressed(Point p) {
        startPoint = p;
    }

    public Figure onMouseDragged(Point current, ToggleButton tool, boolean selectionMode) {
        if (!selectionMode && strategies.containsKey(tool)) {
            previewFigure = strategies.get(tool).apply(startPoint, current);
            return previewFigure;
        }
        return null;
    }

    public Figure onMouseReleased(Point end, ToggleButton tool, boolean selectionMode) {
        if (!selectionMode && strategies.containsKey(tool)) {
            return strategies.get(tool).apply(startPoint, end);
        }
        return null;
    }

    public Figure getSelectedFigure() {
        return selectedFigure;
    }

    public void setSelectedFigure(Figure figure) {
        this.selectedFigure = figure;
    }
}
