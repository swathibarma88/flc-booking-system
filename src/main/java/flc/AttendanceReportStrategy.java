package flc;

import java.util.Comparator;
import java.util.List;

/**
 * Concrete Strategy — prints attendance and average rating for every lesson.
 */
public class AttendanceReportStrategy implements ReportStrategy {

    @Override
    public void generate(List<Timetable> timetables) {

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║      ATTENDANCE & RATING REPORT (ALL WEEKS)      ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        timetables.stream()
                .sorted(Comparator.comparingInt(Timetable::getWeekNumber)
                        .thenComparing(Timetable::getDay))
                .forEach(t -> {

                    System.out.printf("%nWeek %d — %s%n", t.getWeekNumber(), t.getDay());
                    System.out.println("  ──────────────────────────────────────────────────");

                    for (Lesson l : t.getLessons()) {

                        String ratingOutput;

                        if (l.getReviews().isEmpty()) {
                            ratingOutput = "n/a";
                        } else {
                            double avg = l.getAverageRating();
                            ratingOutput = String.format("%.1f/5 (%s)",
                                    avg,
                                    toLabel(avg));
                        }

                        System.out.printf(
                                "  %-22s %-12s  Members: %d/%d   Avg rating: %s%n",
                                l.getTimeSlot().getLabel(),
                                l.getExerciseType().getDisplayName(),
                                l.getEnrolledCount(),
                                Lesson.CAPACITY,
                                ratingOutput
                        );
                    }
                });
    }

    /**
     * Converts numeric rating into human-readable label.
     */
    private String toLabel(double avg) {
        if (avg >= 4.5) return "Very satisfied";
        if (avg >= 3.5) return "Satisfied";
        if (avg >= 2.5) return "Ok";
        if (avg >= 1.5) return "Dissatisfied";
        return "Very dissatisfied";
    }
}