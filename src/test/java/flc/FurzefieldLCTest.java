package flc;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class FurzefieldLCTest {

    private FurzefieldLC system;
    private Member alice, bob, carol;
    private Lesson lesson1, lesson2, lesson3, lesson4;

    @BeforeEach
    void setUp() {
        system = new FurzefieldLC();
        alice = new Member("M001", "Alice");
        bob   = new Member("M002", "Bob");
        carol = new Member("M003", "Carol");
        system.addMember(alice); system.addMember(bob); system.addMember(carol);

        Timetable sat1 = new Timetable(1, Day.SATURDAY);
        lesson1 = new Lesson("L001", ExerciseType.YOGA,      TimeSlot.MORNING,   Day.SATURDAY, 1);
        lesson2 = new Lesson("L002", ExerciseType.BOX_FIT,   TimeSlot.AFTERNOON, Day.SATURDAY, 1);
        lesson3 = new Lesson("L003", ExerciseType.AQUACISE,  TimeSlot.EVENING,   Day.SATURDAY, 1);
        sat1.addLesson(lesson1); sat1.addLesson(lesson2); sat1.addLesson(lesson3);
        system.addTimetable(sat1);

        Timetable sun1 = new Timetable(1, Day.SUNDAY);
        lesson4 = new Lesson("L004", ExerciseType.ZUMBA,        TimeSlot.MORNING,   Day.SUNDAY, 1);
        Lesson l5 = new Lesson("L005", ExerciseType.PILATES,    TimeSlot.AFTERNOON, Day.SUNDAY, 1);
        Lesson l6 = new Lesson("L006", ExerciseType.BODY_BLITZ, TimeSlot.EVENING,   Day.SUNDAY, 1);
        sun1.addLesson(lesson4); sun1.addLesson(l5); sun1.addLesson(l6);
        system.addTimetable(sun1);
    }

    // ── Booking ──────────────────────────────────────────────────────────────

    @Test @DisplayName("1. Member can book a lesson with available space")
    void testBookLesson_success() {
        Booking b = alice.bookLesson(lesson1);
        assertNotNull(b); assertEquals(alice, b.getMember());
        assertEquals(lesson1, b.getLesson()); assertEquals(1, lesson1.getEnrolledCount());
        assertTrue(alice.getBookings().contains(b));
    }

    @Test @DisplayName("2. Lesson capacity is enforced at 4 members")
    void testLessonCapacity() {
        new Member("T1","T1").bookLesson(lesson1); new Member("T2","T2").bookLesson(lesson1);
        new Member("T3","T3").bookLesson(lesson1); new Member("T4","T4").bookLesson(lesson1);
        assertFalse(lesson1.hasSpace());
        assertThrows(IllegalStateException.class, () -> new Member("T5","T5").bookLesson(lesson1));
    }

    @Test @DisplayName("3. Time conflict is detected within same day/week/slot")
    void testTimeConflict() {
        alice.bookLesson(lesson1);
        Lesson dup = new Lesson("L999", ExerciseType.ZUMBA, TimeSlot.MORNING, Day.SATURDAY, 1);
        assertThrows(IllegalStateException.class, () -> alice.bookLesson(dup));
    }

    @Test @DisplayName("4. No time conflict for different day or different week")
    void testNoConflictDifferentDayOrWeek() {
        alice.bookLesson(lesson1); alice.bookLesson(lesson4);
        Lesson w2 = new Lesson("W2SM", ExerciseType.YOGA, TimeSlot.MORNING, Day.SATURDAY, 2);
        assertDoesNotThrow(() -> alice.bookLesson(w2));
    }

    @Test @DisplayName("5. Member can book multiple lessons with no conflict")
    void testMultipleBookings() {
        alice.bookLesson(lesson1); alice.bookLesson(lesson2); alice.bookLesson(lesson3);
        assertEquals(3, alice.getBookings().size());
    }

    // ── Change booking ───────────────────────────────────────────────────────

    @Test @DisplayName("6. Member can change a booking to an available lesson")
    void testChangeBooking_success() {
        Booking b = alice.bookLesson(lesson1);
        Booking nb = alice.changeBooking(b, lesson2);
        assertNotNull(nb); assertEquals(lesson2, nb.getLesson());
        assertEquals(0, lesson1.getEnrolledCount()); assertEquals(1, lesson2.getEnrolledCount());
    }

    @Test @DisplayName("7. Change booking fails when target lesson is full")
    void testChangeBooking_fullTarget() {
        new Member("T1","T1").bookLesson(lesson2); new Member("T2","T2").bookLesson(lesson2);
        new Member("T3","T3").bookLesson(lesson2); new Member("T4","T4").bookLesson(lesson2);
        Booking b = alice.bookLesson(lesson1);
        assertThrows(IllegalStateException.class, () -> alice.changeBooking(b, lesson2));
    }

    // ✅ THIS IS THE FIXED TEST (was test 8 — old version had the wrong booking variable)
    @Test @DisplayName("8. Change booking fails when it would create a time conflict")
    void testChangeBooking_conflictFails() {
        alice.bookLesson(lesson1);               // Sat MORNING  — stays in place
        Booking b2 = alice.bookLesson(lesson2);  // Sat AFTERNOON — this is the one we change

        // Try to change the AFTERNOON booking to another MORNING slot.
        // Alice still has lesson1 (morning), so this must throw a conflict.
        // OLD BUG: was trying to change b1 (morning) → another morning.
        //          changeBooking removes b1 first, so no conflict existed. Test always passed 0 throws.
        // FIX:     change b2 (afternoon) → another morning. lesson1 still occupies morning → conflict fires.
        Lesson anotherMorning = new Lesson("L009", ExerciseType.PILATES,
                TimeSlot.MORNING, Day.SATURDAY, 1);
        assertThrows(IllegalStateException.class, () -> alice.changeBooking(b2, anotherMorning));
    }

    // ── Cancel booking ───────────────────────────────────────────────────────

    @Test @DisplayName("9. Member can cancel a booking")
    void testCancelBooking() {
        Booking b = alice.bookLesson(lesson1);
        alice.cancelBooking(b);
        assertEquals(0, alice.getBookings().size());
        assertEquals(0, lesson1.getEnrolledCount());
    }

    // ── Reviews ──────────────────────────────────────────────────────────────

    @Test @DisplayName("10. Member can write a review for an attended lesson")
    void testWriteReview_success() {
        alice.bookLesson(lesson1);
        Review r = alice.writeReview(lesson1, 5, "Brilliant!");
        assertNotNull(r); assertEquals(5, r.getRating());
        assertEquals("Brilliant!", r.getComment()); assertEquals(1, lesson1.getReviews().size());
    }

    @Test @DisplayName("11. Review rating below 1 throws exception")
    void testReview_invalidRatingLow() {
        alice.bookLesson(lesson1);
        assertThrows(IllegalArgumentException.class, () -> alice.writeReview(lesson1, 0, "bad"));
    }

    @Test @DisplayName("12. Review rating above 5 throws exception")
    void testReview_invalidRatingHigh() {
        alice.bookLesson(lesson1);
        assertThrows(IllegalArgumentException.class, () -> alice.writeReview(lesson1, 6, "bad"));
    }

    @Test @DisplayName("13. Member cannot review a lesson they have not attended")
    void testReview_notAttended() {
        assertThrows(IllegalStateException.class, () -> alice.writeReview(lesson1, 4, "nice"));
    }

    @Test @DisplayName("14. Average rating is calculated correctly")
    void testAverageRating() {
        alice.bookLesson(lesson1); bob.bookLesson(lesson1);
        alice.writeReview(lesson1, 4, "Good"); bob.writeReview(lesson1, 2, "Meh");
        assertEquals(3.0, lesson1.getAverageRating(), 0.001);
    }

    @Test @DisplayName("15. Average rating is 0.0 when no reviews exist")
    void testAverageRating_noReviews() {
        assertEquals(0.0, lesson1.getAverageRating(), 0.001);
    }

    // ── Timetable queries ────────────────────────────────────────────────────

    @Test @DisplayName("16. getLessonsByDay returns only Saturday lessons")
    void testGetLessonsByDay() {
        List<Lesson> sat = system.getLessonsByDay(Day.SATURDAY);
        assertEquals(3, sat.size());
        assertTrue(sat.stream().allMatch(l -> l.getDay() == Day.SATURDAY));
    }

    @Test @DisplayName("17. getLessonsByExercise returns correct lessons")
    void testGetLessonsByExercise() {
        List<Lesson> yoga = system.getLessonsByExercise(ExerciseType.YOGA);
        assertEquals(1, yoga.size());
        assertEquals(ExerciseType.YOGA, yoga.get(0).getExerciseType());
    }

    @Test @DisplayName("18. findLesson returns present for valid ID and empty for unknown")
    void testFindLesson() {
        assertTrue(system.findLesson("L001").isPresent());
        assertFalse(system.findLesson("ZZZZ").isPresent());
    }

    // ── Pricing ──────────────────────────────────────────────────────────────

    @Test @DisplayName("19. Same exercise has same price regardless of time slot")
    void testPriceConsistency() {
        Lesson a = new Lesson("Y1", ExerciseType.YOGA, TimeSlot.MORNING, Day.SATURDAY, 1);
        Lesson b = new Lesson("Y2", ExerciseType.YOGA, TimeSlot.EVENING, Day.SUNDAY,   2);
        assertEquals(a.getPrice(), b.getPrice(), 0.001);
    }

    @Test @DisplayName("20. Total lesson income equals price × enrolled count")
    void testTotalIncome() {
        alice.bookLesson(lesson1); bob.bookLesson(lesson1);
        assertEquals(2 * ExerciseType.YOGA.getPrice(), lesson1.getTotalIncome(), 0.001);
    }

    // ── Enum helpers ─────────────────────────────────────────────────────────

    @Test @DisplayName("21. Day.fromString parses correctly and throws on unknown")
    void testDayFromString() {
        assertEquals(Day.SATURDAY, Day.fromString("Saturday"));
        assertEquals(Day.SUNDAY,   Day.fromString("sun"));
        assertThrows(IllegalArgumentException.class, () -> Day.fromString("Monday"));
    }

    @Test @DisplayName("22. ExerciseType.fromString parses correctly")
    void testExerciseTypeFromString() {
        assertEquals(ExerciseType.YOGA,    ExerciseType.fromString("Yoga"));
        assertEquals(ExerciseType.BOX_FIT, ExerciseType.fromString("Box Fit"));
        assertThrows(IllegalArgumentException.class, () -> ExerciseType.fromString("Badminton"));
    }

    // ── Full data load ───────────────────────────────────────────────────────

    @Test @DisplayName("23. Full load: 10 members and 20+ reviews")
    void testFullDataLoad() {
        FurzefieldLC full = DataLoader.load(new FurzefieldLC());
        assertEquals(10, full.getMembers().size());
        long reviews = full.getMembers().stream().flatMap(m -> m.getReviews().stream()).count();
        assertTrue(reviews >= 20, "Expected >=20 reviews, got " + reviews);
    }

    @Test @DisplayName("24. Full load: exactly 48 lessons across 8 weekends")
    void testFullLoad_48Lessons() {
        FurzefieldLC full = DataLoader.load(new FurzefieldLC());
        long total = full.getLessonsByDay(Day.SATURDAY).size()
                + full.getLessonsByDay(Day.SUNDAY).size();
        assertEquals(48, total);
    }

    // ── Income report ────────────────────────────────────────────────────────

    @Test @DisplayName("25. Single lesson income = price × members booked")
    void testIncomePerLesson() {
        alice.bookLesson(lesson1); bob.bookLesson(lesson1);
        assertEquals(2 * ExerciseType.YOGA.getPrice(), lesson1.getTotalIncome(), 0.001);
    }

    @Test @DisplayName("26. IncomeReportStrategy.getTotalIncome sums across multiple lessons")
    void testGetTotalIncome_multipleYogaLessons() {
        Timetable sat2 = new Timetable(2, Day.SATURDAY);
        Lesson yogaW2 = new Lesson("W2SM", ExerciseType.YOGA, TimeSlot.MORNING, Day.SATURDAY, 2);
        sat2.addLesson(yogaW2);
        sat2.addLesson(new Lesson("W2SA", ExerciseType.BOX_FIT,  TimeSlot.AFTERNOON, Day.SATURDAY, 2));
        sat2.addLesson(new Lesson("W2SE", ExerciseType.AQUACISE, TimeSlot.EVENING,   Day.SATURDAY, 2));
        system.addTimetable(sat2);

        alice.bookLesson(lesson1);   // yoga week1: £12
        bob.bookLesson(yogaW2);      // yoga week2: £12
        carol.bookLesson(yogaW2);    // yoga week2: £12

        double expected = 3 * ExerciseType.YOGA.getPrice();  // £36
        assertEquals(expected,
                IncomeReportStrategy.getTotalIncome(system.getTimetables(), ExerciseType.YOGA),
                0.001);
    }

    @Test @DisplayName("27. IncomeReportStrategy.getHighestEarner identifies correct exercise")
    void testGetHighestEarner() {
        // Fill yoga (4 bookings × £12 = £48)
        new Member("T1","T1").bookLesson(lesson1); new Member("T2","T2").bookLesson(lesson1);
        new Member("T3","T3").bookLesson(lesson1); new Member("T4","T4").bookLesson(lesson1);
        // Only 1 Box Fit booking (£11)
        alice.bookLesson(lesson2);
        assertEquals(ExerciseType.YOGA,
                IncomeReportStrategy.getHighestEarner(system.getTimetables()));
    }

    @Test @DisplayName("28. getHighestEarner on full data returns a valid ExerciseType")
    void testGetHighestEarner_fullData() {
        FurzefieldLC full = DataLoader.load(new FurzefieldLC());
        assertNotNull(IncomeReportStrategy.getHighestEarner(full.getTimetables()));
    }

    // ── Strategy pattern ─────────────────────────────────────────────────────

    @Test @DisplayName("29. Report strategy can be swapped at runtime without error")
    void testStrategySwap() {
        ReportStrategy noOp = timetables -> {};
        system.setAttendanceStrategy(noOp);
        system.setIncomeStrategy(noOp);
        assertDoesNotThrow(() -> {
            system.printAttendanceReport();
            system.printIncomeReport();
        });
    }

    @Test @DisplayName("30. Cancel booking frees the lesson space")
    void testCancelBooking_freesSpace() {
        Member m1 = new Member("T1","T1"); m1.bookLesson(lesson1);
        Member m2 = new Member("T2","T2"); m2.bookLesson(lesson1);
        Member m3 = new Member("T3","T3"); m3.bookLesson(lesson1);
        Member m4 = new Member("T4","T4"); m4.bookLesson(lesson1);
        assertFalse(lesson1.hasSpace());
        m1.cancelBooking(m1.getBookings().get(0));
        assertTrue(lesson1.hasSpace());
        assertEquals(0, m1.getBookings().size());
        assertEquals(3, lesson1.getEnrolledCount());
    }
}