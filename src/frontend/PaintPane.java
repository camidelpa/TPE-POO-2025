package frontend;

import backend.CanvasState;
import backend.model.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class PaintPane extends BorderPane {

	// BackEnd
	private final CanvasState canvasState;

	// Canvas y relacionados
	private final Canvas canvas = new Canvas(800, 600);
	private final GraphicsContext gc = canvas.getGraphicsContext2D();

	// Botones Barra Izquierda
	private final ToggleButton selectionButton = new ToggleButton("Seleccionar");
	private final ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	private final ToggleButton circleButton = new ToggleButton("Círculo");
	private final ToggleButton squareButton = new ToggleButton("Cuadrado");
	private final ToggleButton ellipseButton = new ToggleButton("Elipse");
	private final ToggleButton deleteButton = new ToggleButton("Borrar");

	// Selector de color de relleno
	private final ColorPicker fillColorPicker = new ColorPicker(Color.YELLOW);

	// Dibujar una figura
	private Point startPoint;

	// Seleccionar una figura
	private Figure selectedFigure;

	// StatusBar
	private final StatusPane statusPane;

	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;
		ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton, squareButton, ellipseButton, deleteButton};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(90);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);
		}
		VBox buttonsBox = new VBox(10);
		buttonsBox.getChildren().addAll(toolsArr);
		buttonsBox.getChildren().add(fillColorPicker);
		buttonsBox.setPadding(new Insets(5));
		buttonsBox.setStyle("-fx-background-color: #999");
		buttonsBox.setPrefWidth(100);

		canvas.setOnMousePressed(event -> {
			startPoint = new Point(event.getX(), event.getY());
		});

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) {
				return ;
			}
			if(endPoint.x < startPoint.x || endPoint.y < startPoint.y) {
				return ;
			}
			Figure newFigure = null;
			if(rectangleButton.isSelected()) {
				newFigure = new Rectangle(startPoint, endPoint);
			}
			else if(circleButton.isSelected()) {
				double circleRadius = Math.abs(endPoint.x - startPoint.x);
				newFigure = new Circle(startPoint, circleRadius);
			} else if(squareButton.isSelected()) {
				double size = Math.abs(endPoint.x - startPoint.x);
				newFigure = new Square(startPoint, size);
			} else if(ellipseButton.isSelected()) {
				Point centerPoint = new Point(Math.abs(endPoint.x + startPoint.x) / 2, (Math.abs((endPoint.y + startPoint.y)) / 2));
				double sMayorAxis = Math.abs(endPoint.x - startPoint.x);
				double sMinorAxis = Math.abs(endPoint.y - startPoint.y);
				newFigure = new Ellipse(centerPoint, sMayorAxis, sMinorAxis);
			} else {
				return ;
			}
			canvasState.addFigure(newFigure);
			startPoint = null;
			redrawCanvas();
		});

		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			boolean found = false;
			StringBuilder label = new StringBuilder();
			for(Figure figure : canvasState.figures()) {
				if(figureBelongs(figure, eventPoint)) {
					found = true;
					label.append(figure.toString());
				}
			}
			if(found) {
				statusPane.updateStatus(label.toString());
			} else {
				statusPane.updateStatus(eventPoint.toString());
			}
		});

		canvas.setOnMouseClicked(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				boolean found = false;
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				for (Figure figure : canvasState.figures()) {
					if(figureBelongs(figure, eventPoint)) {
						found = true;
						selectedFigure = figure;
						label.append(figure.toString());
					}
				}
				if (found) {
					statusPane.updateStatus(label.toString());
				} else {
					selectedFigure = null;
					statusPane.updateStatus("Ninguna figura encontrada");
				}
				redrawCanvas();
			}
		});

		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				double diffX = (eventPoint.x - startPoint.x) / 100;
				double diffY = (eventPoint.y - startPoint.y) / 100;
				if(selectedFigure instanceof Rectangle rectangle) {
                    rectangle.getTopLeft().x += diffX;
					rectangle.getBottomRight().x += diffX;
					rectangle.getTopLeft().y += diffY;
					rectangle.getBottomRight().y += diffY;
				} else if(selectedFigure instanceof Circle circle) {
                    circle.getCenterPoint().x += diffX;
					circle.getCenterPoint().y += diffY;
				} else if(selectedFigure instanceof Square square) {
                    square.getTopLeft().x += diffX;
					square.getBottomRight().x += diffX;
					square.getTopLeft().y += diffY;
					square.getBottomRight().y += diffY;
				} else if(selectedFigure instanceof Ellipse ellipse) {
                    ellipse.getCenterPoint().x += diffX;
					ellipse.getCenterPoint().y += diffY;
				}
				redrawCanvas();
			}
		});

		deleteButton.setOnAction(event -> {
			if (selectedFigure != null) {
				canvasState.deleteFigure(selectedFigure);
				selectedFigure = null;
				redrawCanvas();
			}
		});

		setLeft(buttonsBox);
		setRight(canvas);
	}

	void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setLineWidth(1);
		for(Figure figure : canvasState.figures()) {
			if(figure == selectedFigure) {
				gc.setStroke(Color.RED);
			} else {
				gc.setStroke(Color.BLACK);
			}
			gc.setFill(fillColorPicker.getValue());
			if(figure instanceof Rectangle rectangle) {
                gc.fillRect(rectangle.getTopLeft().x, rectangle.getTopLeft().y,
						Math.abs(rectangle.getTopLeft().x - rectangle.getBottomRight().x), Math.abs(rectangle.getTopLeft().y - rectangle.getBottomRight().y));
				gc.strokeRect(rectangle.getTopLeft().x, rectangle.getTopLeft().y,
						Math.abs(rectangle.getTopLeft().x - rectangle.getBottomRight().x), Math.abs(rectangle.getTopLeft().y - rectangle.getBottomRight().y));
			} else if(figure instanceof Circle circle) {
                double diameter = circle.getRadius() * 2;
				gc.fillOval(circle.getCenterPoint().x - circle.getRadius(), circle.getCenterPoint().y - circle.getRadius(), diameter, diameter);
				gc.strokeOval(circle.getCenterPoint().x - circle.getRadius(), circle.getCenterPoint().y - circle.getRadius(), diameter, diameter);
			} else if(figure instanceof Square square) {
                gc.fillRect(square.getTopLeft().x, square.getTopLeft().y,
						Math.abs(square.getTopLeft().x - square.getBottomRight().x), Math.abs(square.getTopLeft().y - square.getBottomRight().y));
				gc.strokeRect(square.getTopLeft().x, square.getTopLeft().y,
						Math.abs(square.getTopLeft().x - square.getBottomRight().x), Math.abs(square.getTopLeft().y - square.getBottomRight().y));
			} else if(figure instanceof Ellipse ellipse) {
                gc.strokeOval(ellipse.getCenterPoint().x - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPoint().y - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
				gc.fillOval(ellipse.getCenterPoint().x - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPoint().y - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
			}
		}
	}

	boolean figureBelongs(Figure figure, Point eventPoint) {
		boolean found = false;
		if(figure instanceof Rectangle rectangle) {
            found = eventPoint.x > rectangle.getTopLeft().x && eventPoint.x < rectangle.getBottomRight().x &&
					eventPoint.y > rectangle.getTopLeft().y && eventPoint.y < rectangle.getBottomRight().y;
		} else if(figure instanceof Circle circle) {
            found = Math.sqrt(Math.pow(circle.getCenterPoint().x - eventPoint.x, 2) +
					Math.pow(circle.getCenterPoint().y - eventPoint.y, 2)) < circle.getRadius();
		} else if(figure instanceof Square square) {
            found = eventPoint.x > square.getTopLeft().x && eventPoint.x < square.getBottomRight().x &&
					eventPoint.y > square.getTopLeft().y && eventPoint.y < square.getBottomRight().y;
		} else if(figure instanceof Ellipse ellipse) {
            // Nota: Fórmula aproximada. No es necesario corregirla.
			found = ((Math.pow(eventPoint.x - ellipse.getCenterPoint().x, 2) / Math.pow(ellipse.getsMayorAxis(), 2)) +
					(Math.pow(eventPoint.y - ellipse.getCenterPoint().y, 2) / Math.pow(ellipse.getsMinorAxis(), 2))) <= 0.30;
		}
		return found;
	}

}
