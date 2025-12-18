package frontend.managers;

import backend.CanvasState;
import backend.model.Figure;
import frontend.StatusPane;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LayerManager extends HBox {


    private final CanvasState canvasState;
    private final StatusPane statusPane;
    private final Runnable redrawCallback;


    private int currentLayer = 0;
    private int nextLayerId = 3;
    private final Map<Integer, Boolean> layersVisibility = new HashMap<>();
    private final List<Integer> availableLayers = new ArrayList<>();

    // Componentes de UI
    private final ChoiceBox<String> layersBox = new ChoiceBox<>();
    private final RadioButton showLayerRb = new RadioButton("Mostrar");
    private final RadioButton hideLayerRb = new RadioButton("Ocultar");
    private final ToggleGroup visibilityGroup = new ToggleGroup();
    private final Button addLayerBtn = new Button("Agregar Capa");
    private final Button deleteLayerBtn = new Button("Eliminar Capa");

    public LayerManager(CanvasState canvasState, StatusPane statusPane, Runnable redrawCallback) {
        this.canvasState = canvasState;
        this.statusPane = statusPane;
        this.redrawCallback = redrawCallback;

        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);

        initializeState();
        initializeUI();
    }

    private void initializeState() {
        // Configuración inicial de capas (0, 1, 2)
        availableLayers.add(0); availableLayers.add(1); availableLayers.add(2);
        layersVisibility.put(0, true); layersVisibility.put(1, true); layersVisibility.put(2, true);
    }

    private void initializeUI() {
        // Configuración del ChoiceBox
        updateLayersBox();
        layersBox.getSelectionModel().selectFirst();

        // Listener de cambio de capa
        layersBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0 && newVal.intValue() < availableLayers.size()) {
                currentLayer = availableLayers.get(newVal.intValue());
                boolean isVisible = layersVisibility.get(currentLayer);
                showLayerRb.setSelected(isVisible);
                hideLayerRb.setSelected(!isVisible);
            }
        });

        // Configuración de visibilidad
        showLayerRb.setToggleGroup(visibilityGroup);
        hideLayerRb.setToggleGroup(visibilityGroup);
        showLayerRb.setSelected(true);

        showLayerRb.setOnAction(e -> {
            layersVisibility.put(currentLayer, true);
            redrawCallback.run();
        });

        hideLayerRb.setOnAction(e -> {
            layersVisibility.put(currentLayer, false);
            redrawCallback.run();
        });

        // Botón Agregar
        addLayerBtn.setOnAction(e -> {
            int newId = nextLayerId++;
            availableLayers.add(newId);
            layersVisibility.put(newId, true);
            updateLayersBox();
            layersBox.getSelectionModel().selectLast();
        });

        // Botón Eliminar
        deleteLayerBtn.setOnAction(e -> handleDeleteLayer());


        layersBox.setTooltip(new Tooltip("Capas: Elija en qué capa dibujar"));
        addLayerBtn.setTooltip(new Tooltip("Nueva Capa: Crea una capa transparente encima de las actuales"));
        deleteLayerBtn.setTooltip(new Tooltip("Eliminar Capa: Borra la capa actual y todas sus figuras"));


        this.getChildren().addAll(
                new Label("Capas:"),
                layersBox,
                showLayerRb,
                hideLayerRb,
                addLayerBtn,
                deleteLayerBtn
        );
    }

    private void handleDeleteLayer() {
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

                redrawCallback.run();
                statusPane.updateStatus("Capa eliminada");
            }
        });
    }

    private void updateLayersBox() {
        String selected = layersBox.getValue();
        layersBox.getItems().clear();
        availableLayers.sort(java.util.Comparator.naturalOrder());
        for (Integer id : availableLayers) {
            layersBox.getItems().add("Capa " + (id + 1));
        }
        if (selected != null && layersBox.getItems().contains(selected)) {
            layersBox.setValue(selected);
        } else if (!layersBox.getItems().isEmpty()) {
            layersBox.getSelectionModel().selectLast();
        }
    }


    public int getCurrentLayer() {
        return currentLayer;
    }

    public boolean isLayerVisible(int layerId) {
        return layersVisibility.getOrDefault(layerId, true);
    }
}