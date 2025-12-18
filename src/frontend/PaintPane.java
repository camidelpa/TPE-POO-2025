package frontend;

import backend.CanvasState;
import backend.model.*;
import frontend.managers.*;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PaintPane extends BorderPane {

    private final Canvas canvas = new Canvas(900, 780);
    private final CanvasState canvasState;
    private final StatusPane statusPane;

    private final ToggleButton selectBtn = new ToggleButton("Seleccionar");
    private final ToggleButton rectBtn = new ToggleButton("Rectángulo");
    private final ToggleButton circleBtn = new ToggleButton("Círculo");
    private final ToggleButton squareBtn = new ToggleButton("Cuadrado");
    private final ToggleButton ellipseBtn = new ToggleButton("Elipse");
    private final ToggleButton deleteBtn = new ToggleButton("Borrar");

    private final ColorPicker fill1 = new ColorPicker(Color.CYAN);
    private final ColorPicker fill2 = new ColorPicker(Color.RED);
    private final ChoiceBox<ShadowType> shadowBox = new ChoiceBox<>();
    private final ChoiceBox<BorderType> borderBox = new ChoiceBox<>();
    private final Slider borderSlider = new Slider(1, 20, 1);

    private final TextArea tagsArea = new TextArea();
    private final Button saveTagsBtn = new Button("Guardar");

    private final RadioButton allRb = new RadioButton("Todas");
    private final RadioButton soloRb = new RadioButton("Solo");
    private final TextField filterField = new TextField();

    private final ToolController toolController;
    private final CanvasRenderer renderer;
    private final TagEditor tagEditor = new TagEditor();
    private final TagFilter tagFilter = new TagFilter();
    private final KeyboardShortcutManager keyManager = new KeyboardShortcutManager();
    private final LayerManager layerManager;

    public PaintPane(CanvasState canvasState, StatusPane statusPane) {
        this.canvasState = canvasState;
        this.statusPane = statusPane;

        Map<ToggleButton, BiFunction<Point, Point, Figure>> strategies = new HashMap<>();
        strategies.put(rectBtn, Rectangle::new);
        strategies.put(circleBtn, Circle::new);
        strategies.put(squareBtn, Square::new);
        strategies.put(ellipseBtn, Ellipse::new);

        toolController = new ToolController(strategies);
        layerManager = new LayerManager(canvasState, statusPane, this::redraw);
        renderer = new CanvasRenderer(
                canvas.getGraphicsContext2D(),
                layerManager,
                tagFilter
        );


        configureUI();
        configureEvents();
        redraw();
    }

    private void configureUI() {

        ToggleGroup tools = new ToggleGroup();
        selectBtn.setToggleGroup(tools);
        rectBtn.setToggleGroup(tools);
        circleBtn.setToggleGroup(tools);
        squareBtn.setToggleGroup(tools);
        ellipseBtn.setToggleGroup(tools);
        deleteBtn.setToggleGroup(tools);

        shadowBox.getItems().addAll(ShadowType.values());
        shadowBox.setValue(ShadowType.NONE);

        borderBox.getItems().addAll(BorderType.values());
        borderBox.setValue(BorderType.NORMAL);

        VBox sidebar = new VBox(8,
                selectBtn, rectBtn, circleBtn, squareBtn, ellipseBtn, deleteBtn,
                new Label("Sombra"), shadowBox,
                new Label("Relleno"), fill1, fill2,
                new Label("Borde"), borderSlider, borderBox,
                new Label("Etiquetas"), tagsArea, saveTagsBtn
        );
        sidebar.setPadding(new Insets(6));
        sidebar.setPrefWidth(115);
        sidebar.setStyle("-fx-background-color:#999");

        ToggleGroup filterGroup = new ToggleGroup();
        allRb.setToggleGroup(filterGroup);
        soloRb.setToggleGroup(filterGroup);
        allRb.setSelected(true);

        HBox topBar = new HBox(10,
                new Label("Mostrar etiquetas:"), allRb, soloRb, filterField,
                layerManager
        );
        topBar.setPadding(new Insets(8));
        topBar.setStyle("-fx-background-color:#999");

        setLeft(sidebar);
        setTop(topBar);
        setCenter(canvas);
    }

    private void configureEvents() {

        canvas.setOnMousePressed(e ->
                toolController.onMousePressed(new Point(e.getX(), e.getY()))
        );

        canvas.setOnMouseDragged(e -> {

            Figure preview = toolController.onMouseDragged(
                    new Point(e.getX(), e.getY()),
                    (ToggleButton) selectBtn.getToggleGroup().getSelectedToggle(),
                    selectBtn.isSelected()
            );

            renderer.redraw(
                    canvasState.figures(),
                    toolController.getSelectedFigure(),
                    preview,
                    false,
                    filterField.getText()
            );
        });


        canvas.setOnMouseReleased(e -> {
            Figure created = toolController.onMouseReleased(
                    new Point(e.getX(), e.getY()),
                    (ToggleButton) selectBtn.getToggleGroup().getSelectedToggle(),
                    selectBtn.isSelected()
            );

            if (created != null) {
                applyStyles(created);
                canvasState.addFigure(created);
            }
            redraw();
        });

        canvas.setOnMouseClicked(e -> {
            if (selectBtn.isSelected()) {
                Figure selected = findFigureAt(
                        new Point(e.getX(), e.getY())
                );
                toolController.setSelectedFigure(selected);
                tagsArea.setText(selected != null ? selected.getTagsString() : "");
                redraw();
            }
        });

        saveTagsBtn.setOnAction(e -> {
            Figure sel = toolController.getSelectedFigure();
            if (sel != null) {
                tagEditor.applyTags(sel, tagsArea.getText());
                statusPane.updateStatus("Etiquetas guardadas");
            }
        });

        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e ->
                keyManager.handle(
                        e.getCode(),
                        toolController.getSelectedFigure(),
                        canvasState,
                        this::redraw,
                        statusPane::updateStatus
                )
        );
    }

    private void applyStyles(Figure f) {
        f.setFillColor1(fill1.getValue());
        f.setFillColor2(fill2.getValue());
        f.setShadowType(shadowBox.getValue());
        f.setBorderType(borderBox.getValue());
        f.setBorderWidth(borderSlider.getValue());
        f.setLayer(layerManager.getCurrentLayer());
    }

    private void redraw() {
        redraw(null);
    }

    private void redraw(Figure preview) {
        renderer.redraw(
                canvasState.figures(),
                toolController.getSelectedFigure(),
                preview,
                false,
                filterField.getText()
        );
    }

    private Figure findFigureAt(Point p) {
        for (Figure f : canvasState.figures()) {
            if (f.contains(p)) {
                return f;
            }
        }
        return null;
    }

}
