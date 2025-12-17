package backend.model;

public enum BorderType {
    NORMAL("Normal"),
    DOTTED_SIMPLE("Simple"),
    DOTTED_COMPLEX("Complejo");

    private final String label;

    BorderType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}