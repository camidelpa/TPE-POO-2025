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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PaintPane extends BorderPane {

	// canvas and state
	private final CanvasState canvasState;
	private final Canvas canvas = new Canvas(800, 600);
	private final GraphicsContext gc = canvas.getGraphicsContext2D();

	// tools
	private final ToggleButton selectionButton = new ToggleButton("Seleccionar");
	private final ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	private final ToggleButton circleButton = new ToggleButton("Círculo");
	private final ToggleButton squareButton = new ToggleButton("Cuadrado");
	private final ToggleButton ellipseButton = new ToggleButton("Elipse");
	private final ToggleButton deleteButton = new ToggleButton("Borrar");

	private final Button duplicateButton = new Button("Duplicar");
	private final Button divideButton = new Button("Dividir");
	private final Button centerButton = new Button("Al Centro");

	// creation strategies map to reduce imperativity
	private final Map<ToggleButton, BiFunction<Point, Point, Figure>> creationStrategies = new HashMap<>();

	// shadows
	private final ChoiceBox<ShadowType> shadowBox = new ChoiceBox<>();

	// fills
	private final ColorPicker fillColorPicker1 = new ColorPicker(Color.CYAN);
	private final ColorPicker fillColorPicker2 = new ColorPicker(Color.RED);

	// borders
	private final ChoiceBox<BorderType> borderBox = new ChoiceBox<>();
	private final Slider borderSlider = new Slider(1, 20, 1);

	private Point startPoint;
	private Figure selectedFigure;
	private final StatusPane statusPane;
	private Figure previewFigure;

	// layer management (not implemented in UI yet)
	private int currentLayer = 0;
	private final Map<Integer, Boolean> layersVisibility = new HashMap<>();
	private final List<Integer> availableLayers = new ArrayList<>();
	private int nextLayerId = 3;

	// layer UI components
	private final ChoiceBox<String> layersBox = new ChoiceBox<>();
	private final ToggleGroup visibilityGroup = new ToggleGroup();
	private final RadioButton showLayerRb = new RadioButton("Mostrar");
	private final RadioButton hideLayerRb = new RadioButton("Ocultar");
	private final Button addLayerBtn = new Button("Agregar Capa");
	private final Button deleteLayerBtn = new Button("Eliminar Capa");

	private final String[] rdmMessages = {
			"Estado actual: objeto == null \uD83D\uDE31\u200B\uD83D\uDE2D",
			"\uD83D\uDE80 El heap está listo para crear objetos \uD83D\uDE80",
			"Fábrica de figuras en standby \uD83D\uDCA4 \uD83D\uDCA4 \uD83D\uDCA4",
			"Polimorfismo en pausa ⏸",
			"🎨 El canvas espera su próxima obra de arte 🎨",
			"\uD83D\uDC4D Listo para recibir un new Figura() \uD83D\uDC4D",
			"⚠ Modo edición deshabilitado (por ahora) ⚠",
			"⌛Interfaz esperando interacción⌛",
			"Garbage Collector aburrido 😴"
	};

	// dark mode toggle
	private final ToggleButton themeToggle = new ToggleButton("🌙");

    private final TextArea tagsArea = new TextArea();
    private final Button saveTagsBtn = new Button("Guardar");

    private final ToggleGroup filterGroup = new ToggleGroup();
    private final RadioButton allFilterRb = new RadioButton("Todas");
    private final RadioButton soloFilterRb = new RadioButton("Solo");
    private final TextField filterField = new TextField();

	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;

		// Initialize creation strategies (removes if/else from createFigure)
		creationStrategies.put(rectangleButton, Rectangle::new);
		creationStrategies.put(circleButton, Circle::new);
		creationStrategies.put(squareButton, Square::new);
		creationStrategies.put(ellipseButton, Ellipse::new);

		ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton, squareButton, ellipseButton, deleteButton};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(90);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);
		}

		// shadows
		shadowBox.getItems().addAll(ShadowType.values());
		shadowBox.setValue(ShadowType.NONE);
		shadowBox.setTooltip(new Tooltip("Tipo de Sombra"));

		// fills
		fillColorPicker1.setMaxWidth(90);
		fillColorPicker2.setMaxWidth(90);

		// borders
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

		divideButton.setOnAction(event -> {
			if (selectedFigure != null) {
				java.util.List<Figure> dividedFigures = selectedFigure.divide();

				canvasState.deleteFigure(selectedFigure);

				for (Figure fig : dividedFigures) {
					canvasState.addFigure(fig);
				}
				selectedFigure = null;
				redrawCanvas();
				statusPane.updateStatus("Figura dividida");
			}
		});

		centerButton.setOnAction(event -> {
			if (selectedFigure != null) {
				selectedFigure.moveToCenter(canvas.getWidth(), canvas.getHeight());
				redrawCanvas();
				statusPane.updateStatus("Figura movida al centro");
			}
		});

		// layer management setup
		availableLayers.add(0); availableLayers.add(1); availableLayers.add(2);
		layersVisibility.put(0, true); layersVisibility.put(1, true); layersVisibility.put(2, true);

		// ChoiceBox configuration
		updateLayersBox();
		layersBox.getSelectionModel().selectFirst();

		layersBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal.intValue() >= 0) {
				currentLayer = availableLayers.get(newVal.intValue());
				boolean isVisible = layersVisibility.get(currentLayer);
				showLayerRb.setSelected(isVisible);
				hideLayerRb.setSelected(!isVisible);
			}
		});

		// show/hide buttons
		showLayerRb.setToggleGroup(visibilityGroup);
		hideLayerRb.setToggleGroup(visibilityGroup);
		showLayerRb.setSelected(true);

		showLayerRb.setOnAction(e -> { layersVisibility.put(currentLayer, true); redrawCanvas(); });
		hideLayerRb.setOnAction(e -> { layersVisibility.put(currentLayer, false); redrawCanvas(); });

		// add layer button
		addLayerBtn.setOnAction(e -> {
			int newId = nextLayerId++;
			availableLayers.add(newId);
			layersVisibility.put(newId, true);
			updateLayersBox();
			layersBox.getSelectionModel().selectLast();
		});

		// delete layer button
		deleteLayerBtn.setOnAction(e -> {
			if (currentLayer <= 2) {
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Acción no permitida");
				alert.setHeaderText("No se pueden eliminar las capas iniciales");
				alert.setContentText("Las capas 1, 2 y 3 son fijas y necesarias para el sistema.");
				alert.showAndWait();
				return;
			}

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Eliminar Capa");
			alert.setHeaderText("¿Estás seguro de eliminar la Capa " + (currentLayer + 1) + "?");
			alert.setContentText("Esta acción borrará todas las figuras de la capa y no se puede deshacer");

			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					List<Figure> toDelete = new ArrayList<>();
					for (Figure f : canvasState.figures()) {
						if (f.getLayer() == currentLayer) toDelete.add(f);
					}
					for (Figure f : toDelete) canvasState.deleteFigure(f);

					layersVisibility.remove(currentLayer);
					availableLayers.remove((Integer) currentLayer);

					updateLayersBox();
					layersBox.getSelectionModel().selectFirst();
					redrawCanvas();
					statusPane.updateStatus("Capa eliminada");
				}
			});
		});

		// tool help messages
		setToolHelp(selectionButton, "Seleccionar: Haga clic en una figura para editarla o arrastre para moverla");
		setToolHelp(rectangleButton, "Rectángulo: Arrastre el mouse para crear");
		setToolHelp(circleButton, "Círculo: Arrastre desde el centro para definir el radio");
		setToolHelp(squareButton, "Cuadrado: Arrastre para definir el tamaño");
		setToolHelp(ellipseButton, "Elipse: Arrastre el área que contendrá la elipse");
		setToolHelp(deleteButton, "Borrar: Elimina la figura seleccionada permanentemente");

		setToolHelp(duplicateButton, "Duplicar: Crea una copia exacta de la figura seleccionada");
		setToolHelp(divideButton, "Dividir: Parte la figura seleccionada en dos mitades");
		setToolHelp(centerButton, "Centrar: Mueve la figura seleccionada al centro del lienzo");

		setToolHelp(shadowBox, "Sombra: Elija un estilo de sombra para la figura");
		setToolHelp(fillColorPicker1, "Color Primario: Relleno principal o inicio del degradado");
		setToolHelp(fillColorPicker2, "Color Secundario: Color final del degradado");
		setToolHelp(borderSlider, "Grosor: Ajuste el ancho del borde");
		setToolHelp(borderBox, "Tipo de Borde: Elija el estilo de línea del contorno");

		setToolHelp(layersBox, "Capas: Elija en qué capa dibujar");
		setToolHelp(addLayerBtn, "Nueva Capa: Crea una capa transparente encima de las actuales");
		setToolHelp(deleteLayerBtn, "Eliminar Capa: Borra la capa actual y todas sus figuras");

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
        buttonsBox.getChildren().addAll(new Label("Etiquetas"), tagsArea, saveTagsBtn);

		Button helpBtn = new Button("❓");
		// help button styling with CSS for circular shape :)
		helpBtn.setStyle(
				"-fx-background-radius: 50em; " +
						"-fx-min-width: 25px; " +
						"-fx-min-height: 25px; " +
						"-fx-max-width: 25px; " +
						"-fx-max-height: 25px; " +
						"-fx-padding: 0; " +
						"-fx-alignment: center; " +
						"-fx-font-size: 12px; " +
						"-fx-cursor: hand; " +
						"-fx-background-color: #A999F0; " +
						"-fx-text-fill: white;"
		);
		setToolHelp(helpBtn, "Ayuda: Ver atajos de teclado y créditos");

		helpBtn.setOnAction(event -> {
			Alert info = new Alert(Alert.AlertType.INFORMATION);
			info.setTitle("Ayuda y Atajos");
			info.setHeaderText("Paint JavaFX - Guía Rápida");
			info.setContentText(
					"ATAJOS DE TECLADO:\n" +
							"• ESC: Deseleccionar figura\n" +
							"• SUPR / BACKSPACE: Eliminar figura seleccionada\n\n" +
							"TRUCOS:\n" +
							"• Modo Oscuro: Usa el botón 🌙 para descansar la vista\n\n" +
							"PS:\n" +
							"Diviertase leyendo algunas frases 'Orientadas a Objetos' \uD83D\uDE09 en la barra de estado!\n\n"
			);
			info.getDialogPane().setMinWidth(400);
			info.showAndWait();
		});

		// spacer for topbar
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		// Layout Topbar
		HBox topBar = new HBox(10);
		topBar.setPadding(new Insets(10));
		topBar.setAlignment(Pos.CENTER_LEFT);
		topBar.setStyle("-fx-background-color: #999");
        topBar.getChildren().addAll(new Label("Mostrar Etiquetas:"), allFilterRb, soloFilterRb, filterField);

		// dark mode toggle setup
		themeToggle.setSelected(false);
		setToolHelp(themeToggle, "Cambiar Tema: Alternar entre modo Claro y Oscuro");


		themeToggle.setOnAction(event -> {
			if (themeToggle.isSelected()) {
				// dark mode on
				themeToggle.setText("☀");
				setStyle("-fx-background-color: #2b2b2b;");

				String darkBarStyle = "-fx-background-color: #3c3f41; -fx-padding: 10; -fx-spacing: 10;";
				String sidebarStyle = "-fx-background-color: #3c3f41; -fx-padding: 5; -fx-spacing: 10;";

				topBar.setStyle(darkBarStyle);
				buttonsBox.setStyle(sidebarStyle);
				statusPane.setStyle("-fx-background-color: #3c3f41;");

				// i m using 'Labeled' to include Label and ChoiceBox, but exclude Buttons
				topBar.getChildren().forEach(node -> {
					if (node instanceof Button && "❓".equals(((Button) node).getText())) {
						return;
					}
					if (node instanceof Label || node instanceof RadioButton || node instanceof CheckBox) {
						node.setStyle("-fx-text-fill: white;");
					} else if (node instanceof Button || node instanceof ToggleButton) {
						node.setStyle("-fx-text-fill: black;");
					}
				});

				buttonsBox.getChildren().forEach(node -> {
					if (node instanceof Label || node instanceof RadioButton || node instanceof CheckBox) {
						node.setStyle("-fx-text-fill: white;");
					} else if (node instanceof Button || node instanceof ToggleButton) {
						node.setStyle("-fx-text-fill: black;");
					}
				});

				statusPane.getChildren().forEach(node -> {
					if (node instanceof javafx.scene.control.Labeled) {
						node.setStyle("-fx-text-fill: #f0f0f0; -fx-font-weight: bold;");
					}
				});

			} else {
				// dark mode off
				themeToggle.setText("🌙");
				setStyle("-fx-background-color: white;");

				String lightBarStyle = "-fx-background-color: #999; -fx-padding: 10; -fx-spacing: 10;";
				String sidebarStyle = "-fx-background-color: #999; -fx-padding: 5; -fx-spacing: 10;";

				topBar.setStyle(lightBarStyle);
				buttonsBox.setStyle(sidebarStyle);
				statusPane.setStyle("-fx-background-color: #999;");

				topBar.getChildren().forEach(node -> {
					if (node instanceof Button && "❓".equals(((Button) node).getText())) {
						return;
					}
					if (node instanceof javafx.scene.control.Labeled) {
						node.setStyle("-fx-text-fill: black;");
					}
				});

				buttonsBox.getChildren().forEach(node -> {
					if (node instanceof javafx.scene.control.Labeled) {
						node.setStyle("-fx-text-fill: black;");
					}
				});

				statusPane.getChildren().forEach(node -> {
					if (node instanceof javafx.scene.control.Labeled) {
						node.setStyle("-fx-text-fill: black;");
					}
				});
			}
		});

		topBar.getChildren().addAll(
				new Label("Capas:"), layersBox,
				showLayerRb, hideLayerRb, addLayerBtn,
				deleteLayerBtn, spacer, themeToggle, helpBtn
		);
		setTop(topBar);

		canvas.setOnMousePressed(event -> {
			startPoint = new Point(event.getX(), event.getY());
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
				statusPane.updateStatus(eventPoint.toString()); // show coords
			}

			// cursor change logic
			if (selectionButton.isSelected()) {
				canvas.setCursor(found ? Cursor.HAND : Cursor.DEFAULT);
			} else {
				canvas.setCursor(Cursor.CROSSHAIR);
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
                    tagsArea.setText(selectedFigure.getTagsString());
				} else {
					selectedFigure = null;
                    tagsArea.clear();
					statusPane.updateStatus("Ninguna figura encontrada");
				}
				redrawCanvas();
			}
		});

		canvas.setOnMouseDragged(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());

			// move logic
			if (selectionButton.isSelected() && selectedFigure != null) {
				double diffX = eventPoint.getX() - startPoint.getX();
				double diffY = eventPoint.getY() - startPoint.getY();
				selectedFigure.move(diffX, diffY);

				// update startPoint for continuous movement
				startPoint = eventPoint;

				redrawCanvas();
			}

			// drawing logic
			else if (!selectionButton.isSelected()) {
				previewFigure = createFigure(startPoint, eventPoint);
				redrawCanvas();
			}
		});

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) return;

			// only if we are in drawing mode, not selection
			if (!selectionButton.isSelected()) {
				Figure newFigure = createFigure(startPoint, endPoint);

				if (newFigure != null) {
					canvasState.addFigure(newFigure);
				}
			}

			startPoint = null;
			previewFigure = null; // erasing the ghost preview
			redrawCanvas();
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

		// keyboard 'shortcuts'
		canvas.setFocusTraversable(true);
		canvas.addEventFilter(javafx.scene.input.MouseEvent.ANY, e -> canvas.requestFocus());
		canvas.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case DELETE:
				case BACK_SPACE:
					//  Supr or Backspace: deletes the figure
					if (selectedFigure != null) {
						canvasState.deleteFigure(selectedFigure);
						selectedFigure = null;
						redrawCanvas();
						statusPane.updateStatus("Figura eliminada con teclado");
					}
					break;

				case ESCAPE:
					// Esc: deselects any selected figure
					if (selectedFigure != null) {
						selectedFigure = null;
						redrawCanvas();
						statusPane.updateStatus("Ninguna figura seleccionada");
					}
					break;
			}
		});

        // Configuración Sidebar (Etiquetas)
        tagsArea.setPrefRowCount(3);
        tagsArea.setPromptText("etiqueta1 etiqueta2...");
        saveTagsBtn.setMaxWidth(Double.MAX_VALUE);

        saveTagsBtn.setOnAction(e -> {
            if (selectedFigure != null) {
                // Separa el texto por espacios o saltos de línea [cite: 322, 323]
                String[] parts = tagsArea.getText().trim().split("\\s+");
                List<String> newTags = new ArrayList<>();
                for (String p : parts) {
                    if (!p.isEmpty()) newTags.add(p);
                }
                selectedFigure.replaceTags(newTags); // Reemplaza existentes [cite: 324]
                statusPane.updateStatus("Etiquetas guardadas para " + selectedFigure.getFigureName());
            }
        });

        // Configuración Topbar (Filtrado)
        allFilterRb.setToggleGroup(filterGroup);
        soloFilterRb.setToggleGroup(filterGroup);
        allFilterRb.setSelected(true);

        allFilterRb.setOnAction(e -> redrawCanvas());
        soloFilterRb.setOnAction(e -> redrawCanvas());
        filterField.textProperty().addListener((obs, old, newVal) -> {
            if (soloFilterRb.isSelected()) redrawCanvas();
        });
	}

	private Stop[] getFilledStops(Figure figure) {
		Stop[] stops = {new Stop(0, figure.getFillColor1()), new Stop(1, figure.getFillColor2())};
		if (figure instanceof Ellipse) {
			gc.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, stops));
		} else {
			gc.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops));
		}
		return stops;
	}

	// Draw logic
	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		List<Figure> sortedFigures = new ArrayList<>();
		for (Figure f : canvasState.figures()) {
			sortedFigures.add(f);
		}
		sortedFigures.sort(Comparator.comparingInt(Figure::getLayer));

        for (Figure figure : sortedFigures) {
            // 1. Verificar visibilidad de Capa (Punto 3)
            boolean layerVisible = layersVisibility.getOrDefault(figure.getLayer(), true);

            // 2. Verificar filtro de Etiquetas (Punto 4)
            boolean tagsVisible = true;
            if (soloFilterRb.isSelected()) {
                String filterText = filterField.getText().trim().split("\\s+")[0]; // Solo primera palabra [cite: 328]
                tagsVisible = !filterText.isEmpty() && figure.hasTag(filterText);
            }

            // Si no cumple ambas condiciones, se oculta [cite: 331, 332]
            if (!layerVisible || !tagsVisible) {
                continue;
            }

            if (figure.getShadowType() != ShadowType.NONE) {
                double offset = 10.0;
                double shadowX = 0; double shadowY = 0; Color shadowColor = Color.GRAY;
                switch (figure.getShadowType()) {
                    case SIMPLE: shadowX = offset; shadowY = offset; shadowColor = Color.GRAY; break;
                    case COLORED: shadowX = offset; shadowY = offset; shadowColor = figure.getFillColor1().darker(); break;
                    case SIMPLE_INVERSE: shadowX = -offset; shadowY = -offset; shadowColor = Color.GRAY; break;
                    case COLORED_INVERSE: shadowX = -offset; shadowY = -offset; shadowColor = figure.getFillColor1().darker(); break;
                    default: break;
                }
                gc.setFill(shadowColor); gc.setStroke(Color.TRANSPARENT);
                drawFigureShape(figure, shadowX, shadowY);
        }
			Stop[] stops = getFilledStops(figure);

			if (figure == selectedFigure) {
				gc.setStroke(Color.RED); gc.setLineDashes((double[]) null);
			} else {
				gc.setStroke(Color.BLACK);
				if(figure.getBorderType() == BorderType.DOTTED_SIMPLE) gc.setLineDashes(10d);
				else if(figure.getBorderType() == BorderType.DOTTED_COMPLEX) gc.setLineDashes(30d, 10d, 15d, 10d);
				else gc.setLineDashes((double[]) null);
			}
			gc.setLineWidth(figure.getBorderWidth());

			drawFigureShape(figure, 0, 0);
		}

		if (previewFigure != null) {
			gc.setGlobalAlpha(0.5);

			Stop[] stops = getFilledStops(previewFigure);

			gc.setStroke(Color.BLACK);
			gc.setLineWidth(previewFigure.getBorderWidth());
			if(previewFigure.getBorderType() == BorderType.DOTTED_SIMPLE) gc.setLineDashes(10d);
			else if(previewFigure.getBorderType() == BorderType.DOTTED_COMPLEX) gc.setLineDashes(30d, 10d, 15d, 10d);
			else gc.setLineDashes((double[]) null);

			drawFigureShape(previewFigure, 0, 0);
			gc.setGlobalAlpha(1.0);
		}
	}

	private void drawFigureShape(Figure figure, double offsetX, double offsetY) {

		if(figure instanceof Rectangle) {
			Rectangle rectangle = (Rectangle) figure;

			// Geometry calculations removed from frontend (should use backend methods)
			double width = rectangle.getWidth();
			double height = rectangle.getHeight();

			gc.fillRect(rectangle.getTopLeft().getX() + offsetX, rectangle.getTopLeft().getY() + offsetY, width, height);
			if (offsetX == 0) gc.strokeRect(rectangle.getTopLeft().getX(), rectangle.getTopLeft().getY(), width, height);

		} else if(figure instanceof Ellipse) {
			Ellipse ellipse = (Ellipse) figure;
			double width = ellipse.getsAxisX();
			double height = ellipse.getsAxisY();
			double x = (ellipse.getCenterPoint().getX() - (width / 2)) + offsetX;
			double y = (ellipse.getCenterPoint().getY() - (height / 2)) + offsetY;

			gc.fillOval(x, y, width, height);
			if (offsetX == 0) gc.strokeOval(x - offsetX, y - offsetY, width, height);
		}
	}

	private Figure createFigure(Point start, Point end) {
		// logic moved to backend constructors to remove calculations here

		// Find the selected tool strategy
		ToggleButton selectedTool = (ToggleButton) rectangleButton.getToggleGroup().getSelectedToggle();

		if (creationStrategies.containsKey(selectedTool)) {
			Figure newFigure = creationStrategies.get(selectedTool).apply(start, end);

			if (newFigure != null) {
				newFigure.setFillColor1(fillColorPicker1.getValue());
				newFigure.setFillColor2(fillColorPicker2.getValue());
				newFigure.setShadowType(shadowBox.getValue());
				newFigure.setBorderType(borderBox.getValue());
				newFigure.setBorderWidth(borderSlider.getValue());
				newFigure.setLayer(currentLayer);
			}
			return newFigure;
		}

		return null;
	}

	private void updateLayersBox() {
		// save current selection
		String selected = layersBox.getValue();

		layersBox.getItems().clear();
		availableLayers.sort(java.util.Comparator.naturalOrder());
		for (Integer id : availableLayers) {
			layersBox.getItems().add("Capa " + (id + 1));
		}

		// restore selection if possible
		if (selected != null && layersBox.getItems().contains(selected)) {
			layersBox.setValue(selected);
		} else if (!layersBox.getItems().isEmpty()) {
			layersBox.getSelectionModel().selectLast();
		}
	}

	// tool help messages
	private void setToolHelp(javafx.scene.control.Control tool, String message) {
		tool.setOnMouseEntered(event -> statusPane.updateStatus(message));
		tool.setOnMouseExited(event -> {
			int index = (int) (Math.random() * rdmMessages.length);
			statusPane.updateStatus(rdmMessages[index]);
		});
	}


}