package frontend.managers;

import backend.model.Figure;

public class TagFilter {

    public boolean isVisible(Figure figure, boolean soloMode, String filterText) {
        if (!soloMode) return true;
        if (filterText == null || filterText.isBlank()) return false;
        return figure.hasTag(filterText.trim());
    }
}
