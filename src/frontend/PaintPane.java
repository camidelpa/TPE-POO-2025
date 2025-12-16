package frontend;

import backend.CanvasState;
import backend.model.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public class PaintPane extends BorderPane {

	// backend
	private final CanvasState canvasState;

	// canvas and GraphicsContext
	private final Canvas canvas = new Canvas(800, 600);
	private final GraphicsContext gc = canvas.getGraphicsContext2D();

	// Tools
	private final ToggleButton selectionButton = new ToggleButton("Seleccionar");
	private final ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	private final ToggleButton circleButton = new ToggleButton("Círculo");
	private final ToggleButton squareButton = new ToggleButton("Cuadrado");
	private final ToggleButton ellipseButton = new ToggleButton("Elipse");
	private final ToggleButton deleteButton = new ToggleButton("Borrar");

    private final Button duplicateButton = new Button("Duplicar");
    private final Button divideButton = new Button("Dividir");
    private final Button centerButton = new Button("Al Centro");

	// 1. Shadows
	private final ChoiceBox<ShadowType> shadowBox = new ChoiceBox<>();

	// 2. Fills
	private final ColorPicker fillColorPicker1 = new ColorPicker(Color.YELLOW);
	private final ColorPicker fillColorPicker2 = new ColorPicker(Color.RED);

	// 3. Borders
	private final ChoiceBox<BorderType> borderBox = new ChoiceBox<>();
	private final Slider borderSlider = new Slider(1, 20, 1);

	// Draw a figure
	private Point startPoint;

	// Select a figure
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

		// Shadows
		shadowBox.getItems().addAll(ShadowType.values());
		shadowBox.setValue(ShadowType.NONE);
		shadowBox.setTooltip(new Tooltip("Tipo de Sombra"));

		// Fills
		fillColorPicker1.setMaxWidth(90);
		fillColorPicker2.setMaxWidth(90);

		// Borders
		borderBox.getItems().addAll(BorderType.values());
		borderBox.setValue(BorderType.NORMAL);
		borderSlider.setShowTickMarks(true);
		borderSlider.setShowTickLabels(true);

		shadowBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (selectedFigure != null) {
				selectedFigure.setShadowType(newVal);
				redrawCanvas();
			}
		});

		fillColorPicker1.setOnAction(e -> {
			if (selectedFigure != null) {
				selectedFigure.setFillColor1(fillColorPicker1.getValue());
				redrawCanvas();
			}
		});

		fillColorPicker2.setOnAction(e -> {
			if (selectedFigure != null) {
				selectedFigure.setFillColor2(fillColorPicker2.getValue());
				redrawCanvas();
			}
		});

		borderBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (selectedFigure != null) {
				selectedFigure.setBorderType(newVal);
				redrawCanvas();
			}
		});

		borderSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
			if (selectedFigure != null) {
				selectedFigure.setBorderWidth(newVal.doubleValue());
				redrawCanvas();
			}
		});

        duplicateButton.setMinWidth(90);
        duplicateButton.setCursor(Cursor.HAND);
        divideButton.setMinWidth(90);
        divideButton.setCursor(Cursor.HAND);
        centerButton.setMinWidth(90);
        centerButton.setCursor(Cursor.HAND);


        duplicateButton.setOnAction(event -> {
            if (selectedFigure != null) {
                double offsetX = 20.0;
                double offsetY = 20.0;

                Figure duplicated = selectedFigure.duplicate(offsetX, offsetY);
                canvasState.addFigure(duplicated);
                redrawCanvas();
                statusPane.updateStatus("Figura duplicada");
            }
        });


        centerButton.setOnAction(event -> {
            if (selectedFigure != null) {
                selectedFigure.moveToCenter(canvas.getWidth(), canvas.getHeight());
                redrawCanvas();
                statusPane.updateStatus("Figura movida al centro");
            }
        });



        // Layout Sidebar
		VBox buttonsBox = new VBox(10);
		buttonsBox.setPadding(new Insets(5));
		buttonsBox.setStyle("-fx-background-color: #999");
		buttonsBox.setPrefWidth(100);

		buttonsBox.getChildren().addAll(toolsArr);

		buttonsBox.getChildren().add(new Label("Sombra"));
		buttonsBox.getChildren().add(shadowBox);

		buttonsBox.getChildren().add(new Label("Relleno"));
		buttonsBox.getChildren().add(fillColorPicker1);
		buttonsBox.getChildren().add(fillColorPicker2);

		buttonsBox.getChildren().add(new Label("Borde"));
		buttonsBox.getChildren().add(borderSlider);
		buttonsBox.getChildren().add(borderBox);

        buttonsBox.getChildren().add(new Label("Acciones"));
        buttonsBox.getChildren().add(duplicateButton);
        buttonsBox.getChildren().add(divideButton);
        buttonsBox.getChildren().add(centerButton);



        canvas.setOnMousePressed(event -> {
			startPoint = new Point(event.getX(), event.getY());
		});

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) {
				return ;
			}
			if(endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
				return ;
			}
			Figure newFigure = null;


			if(rectangleButton.isSelected()) {
				newFigure = new Rectangle(startPoint, endPoint);
			}
			else if(circleButton.isSelected()) {
				double circleRadius = Math.abs(endPoint.getX() - startPoint.getX());
				newFigure = new Circle(startPoint, circleRadius);
			} else if(squareButton.isSelected()) {
				double size = Math.abs(endPoint.getX() - startPoint.getX());
				newFigure = new Square(startPoint, size);
			} else if(ellipseButton.isSelected()) {
				Point centerPoint = new Point(Math.abs(endPoint.getX() + startPoint.getX()) / 2, (Math.abs((endPoint.getY() + startPoint.getY())) / 2));
				double sMayorAxis = Math.abs(endPoint.getX() - startPoint.getX());
				double sMinorAxis = Math.abs(endPoint.getY() - startPoint.getY());
				newFigure = new Ellipse(centerPoint, sMayorAxis, sMinorAxis);
			} else {
				return ;
			}

			newFigure.setFillColor1(fillColorPicker1.getValue());
			newFigure.setFillColor2(fillColorPicker2.getValue());
			newFigure.setShadowType(shadowBox.getValue());
			newFigure.setBorderType(borderBox.getValue());
			newFigure.setBorderWidth(borderSlider.getValue());

			canvasState.addFigure(newFigure);
			startPoint = null;
			redrawCanvas();
		});

		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			boolean found = false;
			StringBuilder label = new StringBuilder();
			for(Figure figure : canvasState.figures()) {
				if(figure.contains(eventPoint)) {
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
					if(figure.contains(eventPoint)) {
						found = true;
						selectedFigure = figure;
						label.append(figure.toString());
					}
				}
				if (found) {
					statusPane.updateStatus(label.toString());
					fillColorPicker1.setValue(selectedFigure.getFillColor1());
					fillColorPicker2.setValue(selectedFigure.getFillColor2());
					shadowBox.setValue(selectedFigure.getShadowType());
					borderBox.setValue(selectedFigure.getBorderType());
					borderSlider.setValue(selectedFigure.getBorderWidth());
				} else {
					selectedFigure = null;
					statusPane.updateStatus("Ninguna figura encontrada");
				}
				redrawCanvas();
			}
		});

		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected() && selectedFigure != null) {
				Point eventPoint = new Point(event.getX(), event.getY());
				double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
				double diffY = (eventPoint.getY() - startPoint.getY()) / 100;

				selectedFigure.move(diffX, diffY);
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

	// Draw logic
	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		for(Figure figure : canvasState.figures()) {

			// 1. Draw
			if (figure.getShadowType() != ShadowType.NONE) {
				double offset = 10.0;
				double shadowX = 0;
				double shadowY = 0;
				Color shadowColor = Color.GRAY;

				switch (figure.getShadowType()) {
					case SIMPLE:
						shadowX = offset; shadowY = offset;
						shadowColor = Color.GRAY;
						break;
					case COLORED:
						shadowX = offset; shadowY = offset;
						shadowColor = figure.getFillColor1().darker();
						break;
					case SIMPLE_INVERSE:
						shadowX = -offset; shadowY = -offset;
						shadowColor = Color.GRAY;
						break;
					case COLORED_INVERSE:
						shadowX = -offset; shadowY = -offset;
						shadowColor = figure.getFillColor1().darker();
						break;
					default: break;
				}

				gc.setFill(shadowColor);
				gc.setStroke(Color.TRANSPARENT);
				drawFigureShape(figure, shadowX, shadowY);
			}

			// 2. Gradient
			Stop[] stops = new Stop[] { new Stop(0, figure.getFillColor1()), new Stop(1, figure.getFillColor2()) };

			if (figure instanceof Ellipse) {
				RadialGradient radialGradient = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, stops);
				gc.setFill(radialGradient);
			} else {
				// Rectangle y Square (hijo de Rectangle) entran acá
				LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
				gc.setFill(linearGradient);
			}

			// 3. Border
			if (figure == selectedFigure) {
				gc.setStroke(Color.RED);
				gc.setLineDashes((double[]) null);
				gc.setLineWidth(figure.getBorderWidth());
			} else {
				gc.setStroke(Color.BLACK);
				gc.setLineWidth(figure.getBorderWidth());

				switch (figure.getBorderType()) {
					case DOTTED_SIMPLE:
						gc.setLineDashes(10d);
						break;
					case DOTTED_COMPLEX:
						gc.setLineDashes(30d, 10d, 15d, 10d);
						break;
					case NORMAL:
					default:
						gc.setLineDashes((double[]) null);
						break;
				}
			}

			// 4. Draw Figure
			drawFigureShape(figure, 0, 0);
		}
	}

	private void drawFigureShape(Figure figure, double offsetX, double offsetY) {

		if(figure instanceof Rectangle) {
			Rectangle rectangle = (Rectangle) figure;
			double width = Math.abs(rectangle.getTopLeft().getX() - rectangle.getBottomRight().getX());
			double height = Math.abs(rectangle.getTopLeft().getY() - rectangle.getBottomRight().getY());

			gc.fillRect(rectangle.getTopLeft().getX() + offsetX, rectangle.getTopLeft().getY() + offsetY, width, height);
			if (offsetX == 0) gc.strokeRect(rectangle.getTopLeft().getX(), rectangle.getTopLeft().getY(), width, height);

		} else if(figure instanceof Ellipse) {
			Ellipse ellipse = (Ellipse) figure;
			double width = ellipse.getsMayorAxis();
			double height = ellipse.getsMinorAxis();
			double x = (ellipse.getCenterPoint().getX() - (width / 2)) + offsetX;
			double y = (ellipse.getCenterPoint().getY() - (height / 2)) + offsetY;

			gc.fillOval(x, y, width, height);
			if (offsetX == 0) gc.strokeOval(x - offsetX, y - offsetY, width, height);
		}
	}
}