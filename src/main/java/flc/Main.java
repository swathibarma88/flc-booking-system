package flc;

import java.util.*;

/**
 * Entry point. On startup: loads 8 weekends of data, then automatically
 * triggers end-of-period reports after the first 4 weekends as required
 * by the specification. Interactive menu follows.
 */
public class Main {

    private static final int     AUTO_REPORT_WEEKS = 4;
    private static FurzefieldLC  system;
    private static Scanner       scanner = new Scanner(System.in);

    public static void main(String[] args) {
        system = new FurzefieldLC();
        DataLoader.load(system);

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   Furzefield Leisure Centre — Booking System     ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // Specification requirement: after four weekends, print reports automatically
        System.out.printf("%n[System] %d weekends loaded — triggering mandatory end-of-period reports...%n",
                AUTO_REPORT_WEEKS);
        system.printEndOfPeriodReports(AUTO_REPORT_WEEKS);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1"  -> viewTimetableByDay();
                case "2"  -> viewTimetableByExercise();
                case "3"  -> bookLesson();
                case "4"  -> changeBooking();
                case "5"  -> cancelBooking();          // ← NEW
                case "6"  -> writeReview();
                case "7"  -> viewMemberBookings();
                case "8"  -> system.printAttendanceReport();
                case "9"  -> system.printIncomeReport();
                case "10" -> printWeeklyReport();
                case "0"  -> running = false;
                default   -> System.out.println("  Invalid option.");
            }
        }
        System.out.println("\nThank you for using FLC Booking System. Goodbye!");
    }

    private static void printMenu() {
        System.out.println("\n──────────────────────────────────────────────────");
        System.out.println("  1.  View timetable by day");
        System.out.println("  2.  View timetable by exercise");
        System.out.println("  3.  Book a lesson");
        System.out.println("  4.  Change a booking");
        System.out.println("  5.  Cancel a booking");
        System.out.println("  6.  Write a review");
        System.out.println("  7.  View member bookings");
        System.out.println("  8.  Print attendance & rating report");
        System.out.println("  9.  Print income report");
        System.out.println("  10. Print weekly report");
        System.out.println("  0.  Exit");
        System.out.print("  Choose: ");
    }

    private static void viewTimetableByDay() {
        System.out.print("  Day (SATURDAY / SUNDAY): ");
        try { system.printTimetableByDay(Day.fromString(scanner.nextLine())); }
        catch (IllegalArgumentException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void viewTimetableByExercise() {
        System.out.print("  Exercise (Yoga/Zumba/Aquacise/Box Fit/Body Blitz/Pilates): ");
        try { system.printTimetableByExercise(scanner.nextLine()); }
        catch (IllegalArgumentException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void bookLesson() {
        try {
            System.out.print("  Member ID: ");
            Member m = system.findMember(scanner.nextLine().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found."));
            System.out.print("  Lesson ID (e.g. W1SM): ");
            Lesson l = system.findLesson(scanner.nextLine().trim().toUpperCase())
                    .orElseThrow(() -> new IllegalArgumentException("Lesson not found."));
            System.out.println("  ✓ Booked! " + m.bookLesson(l));
        } catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void changeBooking() {
        try {
            System.out.print("  Member ID: ");
            Member m = system.findMember(scanner.nextLine().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found."));
            List<Booking> list = new ArrayList<>(m.getBookings());
            if (list.isEmpty()) { System.out.println("  No bookings."); return; }
            for (int i = 0; i < list.size(); i++)
                System.out.printf("    %d. %s%n", i+1, list.get(i));
            System.out.print("  Select number to change: ");
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            System.out.print("  New Lesson ID: ");
            Lesson nl = system.findLesson(scanner.nextLine().trim().toUpperCase())
                    .orElseThrow(() -> new IllegalArgumentException("Lesson not found."));
            System.out.println("  ✓ Changed! " + m.changeBooking(list.get(idx), nl));
        } catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void cancelBooking() {
        try {
            System.out.print("  Member ID: ");
            Member m = system.findMember(scanner.nextLine().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found."));
            List<Booking> list = new ArrayList<>(m.getBookings());
            if (list.isEmpty()) { System.out.println("  No bookings to cancel."); return; }
            for (int i = 0; i < list.size(); i++)
                System.out.printf("    %d. %s%n", i+1, list.get(i));
            System.out.print("  Select number to cancel: ");
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= list.size()) { System.out.println("  Invalid number."); return; }
            m.cancelBooking(list.get(idx));
            System.out.println("  ✓ Booking cancelled.");
        } catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void writeReview() {
        try {
            System.out.print("  Member ID: ");
            Member m = system.findMember(scanner.nextLine().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found."));

            System.out.print("  Lesson ID: ");
            Lesson l = system.findLesson(scanner.nextLine().trim().toUpperCase())
                    .orElseThrow(() -> new IllegalArgumentException("Lesson not found."));

            System.out.print("  Rating (1-5): ");
            int rating = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("  Comment: ");
            String comment = scanner.nextLine().trim();

            Review r = m.writeReview(l, rating, comment);

            // ✅ THIS is what prints Satisfied / Very satisfied
            System.out.println("  ✓ " + r);

        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }
    private static void viewMemberBookings() {
        try {
            System.out.print("  Member ID: ");
            Member m = system.findMember(scanner.nextLine().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found."));
            System.out.println("\n  Bookings for " + m.getName() + ":");
            if (m.getBookings().isEmpty()) System.out.println("    None.");
            else m.getBookings().forEach(b -> System.out.println("    " + b));
        } catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void printWeeklyReport() {
        System.out.print("  Week number (1-8): ");
        try { system.printWeeklyReport(Integer.parseInt(scanner.nextLine().trim())); }
        catch (NumberFormatException e) { System.out.println("  Invalid number."); }
    }
}
