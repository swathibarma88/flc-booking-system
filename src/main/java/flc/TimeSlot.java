package flc;

public enum TimeSlot {
    MORNING("Morning (9:00am)"),
    AFTERNOON("Afternoon (1:00pm)"),
    EVENING("Evening (6:00pm)");

    private final String label;
    TimeSlot(String label) { this.label = label; }
    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}