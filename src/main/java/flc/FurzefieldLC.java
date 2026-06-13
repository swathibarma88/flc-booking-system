package flc;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Central system class for Furzefield Leisure Centre.
 *
 * DESIGN PATTERN — Strategy:
 *   Report generation is delegated to ReportStrategy implementations.
 *   AttendanceReportStrategy and IncomeReportStrategy can be swapped at
 *   runtime via setAttendanceStrategy() / setIncomeStrategy().
 *   This satisfies the Open/Closed Principle — new report types can be
 *   added without modifying this class.
 *
 * REFACTORING:
 *   printAttendanceReport() and printIncomeReport() were originally 30+
 *   line methods embedded here. They were extracted into dedicated strategy
 *   classes (Extract Class refactoring), and getTimetablesForWeek() was
 *   extracted from repeated inline stream filters (Extract Method refactoring).
 */
public class FurzefieldLC implements Reportable {

    private final List<Timetable> timetables = new ArrayList<>();
    private final List<Member>    members    = new ArrayList<>();

    private ReportStrategy attendanceStrategy = new AttendanceReportStrategy();
    private ReportStrategy incomeStrategy     = new IncomeReportStrategy();

    // ── Strategy setters ────────────────────────────────────────────────────

    public void setAttendanceStrategy(ReportStrategy s) { this.attendanceStrategy = s; }
    public void setIncomeStrategy(ReportStrategy s)     { this.incomeStrategy = s; }

    // ── Timetable management ─────────────────────────────────────────────────

    public void addTimetable(Timetable t) { timetables.add(t); }

    /** Unmodifiable view of all timetables — used by strategies and tests. */
    public List<Timetable> getTimetables() { return Collections.unmodifiableList(timetables); }

    /** Extracted helper (refactored from repeated inline filters). */
    public List<Timetable> getTimetablesForWeek(int weekNumber) {
        return timetables.stream()
                .filter(t -> t.getWeekNumber() == weekNumber)
                .collect(Collectors.toList());
    }

    public List<Lesson> getLessonsByDay(Day day) {
        return timetables.stream().filter(t -> t.getDay() == day)
                .flatMap(t -> t.getLessons().stream()).collect(Collectors.toList());
    }

    public List<Lesson> getLessonsByExercise(ExerciseType type) {
        return timetables.stream().flatMap(t -> t.getLessons().stream())
                .filter(l -> l.getExerciseType() == type).collect(Collectors.toList());
    }

    public List<Lesson> getLessonsByExerciseName(String name) {
        return getLessonsByExercise(ExerciseType.fromString(name));
    }

    public Optional<Lesson> findLesson(String lessonId) {
        return timetables.stream().flatMap(t -> t.getLessons().stream())
                .filter(l -> l.getLessonId().equals(lessonId)).findFirst();
    }

    public void printTimetableByDay(Day day) {
        System.out.println("\n========================================");
        System.out.println("  Timetable for " + day);
        System.out.println("========================================");
        timetables.stream().filter(t -> t.getDay() == day)
                .sorted(Comparator.comparingInt(Timetable::getWeekNumber))
                .forEach(t -> System.out.print(t));
    }

    public void printTimetableByExercise(String exerciseName) {
        System.out.println("\n========================================");
        System.out.println("  Timetable for exercise: " + exerciseName);
        System.out.println("========================================");
        getLessonsByExerciseName(exerciseName).forEach(l -> System.out.println("  " + l));
    }

    // ── Member management ────────────────────────────────────────────────────

    public void addMember(Member m)  { members.add(m); }
    public List<Member> getMembers() { return List.copyOf(members); }

    public Optional<Member> findMember(String memberId) {
        return members.stream().filter(m -> m.getMemberId().equals(memberId)).findFirst();
    }

    // ── Reportable — delegate to strategies ──────────────────────────────────

    @Override
    public void printAttendanceReport() { attendanceStrategy.generate(getTimetables()); }

    @Override
    public void printIncomeReport()     { incomeStrategy.generate(getTimetables()); }

    @Override
    public void printWeeklyReport(int weekNumber) {
        System.out.printf("%n╔══════════════════════════════════════════════════╗%n");
        System.out.printf("║           WEEKLY REPORT — WEEK %-3d               ║%n", weekNumber);
        System.out.printf("╚══════════════════════════════════════════════════╝%n");
        List<Timetable> week = getTimetablesForWeek(weekNumber);
        if (week.isEmpty()) { System.out.println("  No data for week " + weekNumber); return; }
        for (Timetable t : week) {
            System.out.print(t);
            for (Lesson l : t.getLessons()) {
                if (!l.getReviews().isEmpty()) {
                    System.out.printf("    Reviews for %s:%n", l.getExerciseType().getDisplayName());
                    l.getReviews().forEach(r -> System.out.printf("      %s: %d/5 - %s%n",
                            r.getMember().getName(), r.getRating(), r.getComment()));
                }
            }
        }
    }

    /**
     * Automatically triggered after N weekends as required by the specification.
     * Called from Main on startup after 4 weekends of data are loaded.
     */
    public void printEndOfPeriodReports(int weeksCompleted) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.printf( "║   END-OF-PERIOD REPORTS — AFTER %d WEEKEND(S)   ║%n", weeksCompleted);
        System.out.println("╚══════════════════════════════════════════════════╝");
        printAttendanceReport();
        printIncomeReport();
    }
}
