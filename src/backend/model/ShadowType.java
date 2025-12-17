package backend.model;

public enum ShadowType {
    NONE("Normal"),
    SIMPLE("Simple"),
    COLORED("Coloreado"),
    SIMPLE_INVERSE("Simple Inverso"),
    COLORED_INVERSE("Coloreado Inverso");

    private final String label;

    ShadowType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
