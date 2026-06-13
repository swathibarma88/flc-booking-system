package flc;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Concrete Strategy — prints income grouped by exercise type and finds the
 * highest-earning exercise across all weeks.
 */
public class IncomeReportStrategy implements ReportStrategy {

    @Override
    public void generate(List<Timetable> timetables) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║          INCOME REPORT — BY EXERCISE TYPE        ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        List<Lesson> all = timetables.stream()
                .flatMap(t -> t.getLessons().stream())
                .collect(Collectors.toList());

        Map<ExerciseType, Double>  incomeMap  = new EnumMap<>(ExerciseType.class);
        Map<ExerciseType, Integer> bookingMap = new EnumMap<>(ExerciseType.class);

        for (ExerciseType e : ExerciseType.values()) {
            incomeMap.put(e, all.stream().filter(l -> l.getExerciseType() == e)
                    .mapToDouble(Lesson::getTotalIncome).sum());
            bookingMap.put(e, all.stream().filter(l -> l.getExerciseType() == e)
                    .mapToInt(Lesson::getEnrolledCount).sum());
        }

        incomeMap.entrySet().stream()
                .sorted(Map.Entry.<ExerciseType, Double>comparingByValue().reversed())
                .forEach(e -> System.out.printf("  %-12s  Bookings: %3d   Income: £%.2f%n",
                        e.getKey().getDisplayName(), bookingMap.get(e.getKey()), e.getValue()));

        incomeMap.entrySet().stream().max(Map.Entry.comparingByValue())
                .ifPresent(top -> System.out.printf("%n  ★ Highest-earning: %s — £%.2f%n",
                        top.getKey().getDisplayName(), top.getValue()));
    }

    /** Returns the ExerciseType with the highest total income. Used by tests. */
    public static ExerciseType getHighestEarner(List<Timetable> timetables) {
        List<Lesson> all = timetables.stream()
                .flatMap(t -> t.getLessons().stream()).collect(Collectors.toList());
        return Arrays.stream(ExerciseType.values())
                .max(Comparator.comparingDouble(e ->
                        all.stream().filter(l -> l.getExerciseType() == e)
                                .mapToDouble(Lesson::getTotalIncome).sum()))
                .orElseThrow();
    }

    /** Returns total income for a specific exercise type. Used by tests. */
    public static double getTotalIncome(List<Timetable> timetables, ExerciseType type) {
        return timetables.stream().flatMap(t -> t.getLessons().stream())
                .filter(l -> l.getExerciseType() == type)
                .mapToDouble(Lesson::getTotalIncome).sum();
    }
}
