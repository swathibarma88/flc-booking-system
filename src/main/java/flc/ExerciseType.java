package flc;

public enum ExerciseType {
    YOGA      ("Yoga",       12.00),
    ZUMBA     ("Zumba",      10.00),
    AQUACISE  ("Aquacise",    9.00),
    BOX_FIT   ("Box Fit",    11.00),
    BODY_BLITZ("Body Blitz", 13.00),
    PILATES   ("Pilates",    11.50);

    private final String displayName;
    private final double price;

    ExerciseType(String displayName, double price) {
        this.displayName = displayName;
        this.price       = price;
    }

    public String getDisplayName() { return displayName; }
    public double getPrice()       { return price; }

    @Override
    public String toString() { return displayName; }

    public static ExerciseType fromString(String s) {
        for (ExerciseType e : values()) {
            if (e.displayName.equalsIgnoreCase(s.trim()) ||
                    e.name().equalsIgnoreCase(s.trim().replace(" ", "_"))) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown exercise: " + s);
    }
}
