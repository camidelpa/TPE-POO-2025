package frontend.managers;

import backend.CanvasState;
import backend.model.Figure;
import javafx.scene.input.KeyCode;
import java.util.function.Consumer;

public class KeyboardShortcutManager {

    public void handle(
            KeyCode code,
            Figure selected,
            CanvasState canvasState,
            Runnable redraw,
            Consumer<String> status
    ) {
        switch (code) {
            case DELETE, BACK_SPACE -> {
                if (selected != null) {
                    canvasState.deleteFigure(selected);
                    redraw.run();
                    status.accept("Figura eliminada");
                }
            }
            case ESCAPE -> {
                redraw.run();
                status.accept("Selección cancelada");
            }
        }
    }
}
