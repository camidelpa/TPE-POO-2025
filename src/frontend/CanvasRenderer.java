package frontend;

import backend.model.*;
import frontend.managers.LayerManager;

import frontend.managers.TagFilter;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CanvasRenderer {

    private final GraphicsContext gc;
    private final LayerManager layerManager;
    private final TagFilter tagFilter;

    public CanvasRenderer(GraphicsContext gc,
                          LayerManager layerManager,
                          TagFilter tagFilter) {
        this.gc = gc;
        this.layerManager = layerManager;
        this.tagFilter = tagFilter;
    }

    public void redraw(
            Iterable<Figure> figures,
            Figure selectedFigure,
            Figure previewFigure,
            boolean soloMode,
            String filterText
    ) {

        // Limpia el canvas
        gc.clearRect(
                0,
                0,
                gc.getCanvas().getWidth(),
                gc.getCanvas().getHeight()
        );

        // ===============================
        // Orden por capa (layer)
        // ===============================
        List<Figure> ordered = new ArrayList<>();
        figures.forEach(ordered::add);

        ordered.sort(Comparator.comparingInt(Figure::getLayer));

        // ===============================
        // Dibujo de figuras visibles
        // ===============================
        for (Figure figure : ordered) {

            if (!layerManager.isLayerVisible(figure.getLayer())) {
                continue;
            }

            if (!tagFilter.isVisible(figure, soloMode, filterText)) {
                continue;
            }

            drawFigure(figure, figure == selectedFigure);
        }

        // ===============================
        // Preview (figura fantasma)
        // ===============================
        if (previewFigure != null) {
            drawPreview(previewFigure);
        }
    }

    // =================================================
    // Métodos privados de dibujo
    // =================================================

    private void drawFigure(Figure figure, boolean selected) {

        drawShadow(figure);
        applyFill(figure);

        if (selected) {
            gc.setStroke(Color.RED);
            gc.setLineDashes((double[]) null);
        } else {
            gc.setStroke(Color.BLACK);

            if (figure.getBorderType() == BorderType.DOTTED_SIMPLE) {
                gc.setLineDashes(10d);
            } else if (figure.getBorderType() == BorderType.DOTTED_COMPLEX) {
                gc.setLineDashes(30d, 10d, 15d, 10d);
            } else {
                gc.setLineDashes((double[]) null);
            }
        }

        gc.setLineWidth(figure.getBorderWidth());

        drawShape(figure, 0, 0);
    }

    private void drawPreview(Figure preview) {

        gc.setGlobalAlpha(0.5);

        applyFill(preview);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(preview.getBorderWidth());

        drawShape(preview, 0, 0);

        gc.setGlobalAlpha(1.0);
    }

    // =================================================
    // Utilidades
    // =================================================

    private void drawShadow(Figure figure) {

        if (figure.getShadowType() == ShadowType.NONE) return;

        double offset = 10;
        double dx = 0;
        double dy = 0;
        Color color = Color.GRAY;

        switch (figure.getShadowType()) {
            case SIMPLE -> {
                dx = offset;
                dy = offset;
            }
            case COLORED -> {
                dx = offset;
                dy = offset;
                color = figure.getFillColor1().darker();
            }
            case SIMPLE_INVERSE -> {
                dx = -offset;
                dy = -offset;
            }
            case COLORED_INVERSE -> {
                dx = -offset;
                dy = -offset;
                color = figure.getFillColor1().darker();
            }
            default -> {}
        }

        gc.setFill(color);
        gc.setStroke(Color.TRANSPARENT);

        drawShape(figure, dx, dy);
    }

    private void applyFill(Figure figure) {

        Stop[] stops = new Stop[]{
                new Stop(0, figure.getFillColor1()),
                new Stop(1, figure.getFillColor2())
        };

        if (figure instanceof Ellipse) {
            gc.setFill(new RadialGradient(
                    0, 0,
                    0.5, 0.5,
                    0.5,
                    true,
                    CycleMethod.NO_CYCLE,
                    stops
            ));
        } else {
            gc.setFill(new LinearGradient(
                    0, 0,
                    1, 0,
                    true,
                    CycleMethod.NO_CYCLE,
                    stops
            ));
        }
    }

    private void drawShape(Figure figure, double offsetX, double offsetY) {

        if (figure instanceof Rectangle rect) {

            gc.fillRect(
                    rect.getTopLeft().getX() + offsetX,
                    rect.getTopLeft().getY() + offsetY,
                    rect.getWidth(),
                    rect.getHeight()
            );

            if (offsetX == 0 && offsetY == 0) {
                gc.strokeRect(
                        rect.getTopLeft().getX(),
                        rect.getTopLeft().getY(),
                        rect.getWidth(),
                        rect.getHeight()
                );
            }

        } else if (figure instanceof Ellipse ell) {

            double x = ell.getCenterPoint().getX()
                    - ell.getsAxisX() / 2 + offsetX;
            double y = ell.getCenterPoint().getY()
                    - ell.getsAxisY() / 2 + offsetY;

            gc.fillOval(x, y, ell.getsAxisX(), ell.getsAxisY());

            if (offsetX == 0 && offsetY == 0) {
                gc.strokeOval(x, y, ell.getsAxisX(), ell.getsAxisY());
            }
        }
    }
}
