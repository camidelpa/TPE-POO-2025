package frontend.managers;

import backend.model.Figure;
import java.util.ArrayList;
import java.util.List;

public class TagEditor {

    public void applyTags(Figure figure, String rawText) {
        List<String> tags = new ArrayList<>();
        for (String t : rawText.trim().split("\\s+")) {
            if (!t.isEmpty()) {
                tags.add(t);
            }
        }
        figure.replaceTags(tags);
    }
}
