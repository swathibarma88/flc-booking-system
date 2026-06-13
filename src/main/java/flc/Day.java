package flc;

public enum Day {
    SATURDAY, SUNDAY;

    public static Day fromString(String s) {
        return switch (s.trim().toUpperCase()) {
            case "SATURDAY", "SAT" -> SATURDAY;
            case "SUNDAY",   "SUN" -> SUNDAY;
            default -> throw new IllegalArgumentException("Unknown day: " + s);
        };
    }
}