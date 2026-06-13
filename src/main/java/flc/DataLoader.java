package flc;

/**
 * Seeds the system with 8 weekends (48 lessons), 10 members, 21 reviews.
 * Separation of data from logic — Single Responsibility Principle.
 */
public class DataLoader {

    public static FurzefieldLC load(FurzefieldLC system) {

        // ── 10 Members ───────────────────────────────────────────────────────
        Member alice  = new Member("M001", "Alice Johnson");
        Member bob    = new Member("M002", "Bob Smith");
        Member carol  = new Member("M003", "Carol Davies");
        Member dave   = new Member("M004", "Dave Wilson");
        Member emma   = new Member("M005", "Emma Brown");
        Member frank  = new Member("M006", "Frank Turner");
        Member grace  = new Member("M007", "Grace Lee");
        Member harry  = new Member("M008", "Harry Khan");
        Member irene  = new Member("M009", "Irene Patel");
        Member jack   = new Member("M010", "Jack Morris");
        for (Member m : new Member[]{alice,bob,carol,dave,emma,frank,grace,harry,irene,jack})
            system.addMember(m);

        // ── 8 Weekends = 48 lessons ──────────────────────────────────────────
        for (int w = 1; w <= 8; w++) {
            Timetable sat = new Timetable(w, Day.SATURDAY);
            sat.addLesson(new Lesson("W"+w+"SM", SAT_MORNING[w-1],   TimeSlot.MORNING,   Day.SATURDAY, w));
            sat.addLesson(new Lesson("W"+w+"SA", SAT_AFTERNOON[w-1], TimeSlot.AFTERNOON, Day.SATURDAY, w));
            sat.addLesson(new Lesson("W"+w+"SE", SAT_EVENING[w-1],   TimeSlot.EVENING,   Day.SATURDAY, w));
            system.addTimetable(sat);

            Timetable sun = new Timetable(w, Day.SUNDAY);
            sun.addLesson(new Lesson("W"+w+"UM", SUN_MORNING[w-1],   TimeSlot.MORNING,   Day.SUNDAY, w));
            sun.addLesson(new Lesson("W"+w+"UA", SUN_AFTERNOON[w-1], TimeSlot.AFTERNOON, Day.SUNDAY, w));
            sun.addLesson(new Lesson("W"+w+"UE", SUN_EVENING[w-1],   TimeSlot.EVENING,   Day.SUNDAY, w));
            system.addTimetable(sun);
        }

        // ── Bookings ─────────────────────────────────────────────────────────
        Lesson w1sm = system.findLesson("W1SM").get();
        Lesson w1sa = system.findLesson("W1SA").get();
        Lesson w1se = system.findLesson("W1SE").get();
        Lesson w1um = system.findLesson("W1UM").get();
        Lesson w1ua = system.findLesson("W1UA").get();
        Lesson w2sm = system.findLesson("W2SM").get();
        Lesson w2sa = system.findLesson("W2SA").get();
        Lesson w2se = system.findLesson("W2SE").get();
        Lesson w2um = system.findLesson("W2UM").get();
        Lesson w2ua = system.findLesson("W2UA").get();
        Lesson w3sm = system.findLesson("W3SM").get();
        Lesson w3sa = system.findLesson("W3SA").get();
        Lesson w3um = system.findLesson("W3UM").get();
        Lesson w3ua = system.findLesson("W3UA").get();
        Lesson w4sm = system.findLesson("W4SM").get();
        Lesson w4ua = system.findLesson("W4UA").get();

        alice.bookLesson(w1sm); alice.bookLesson(w1um); alice.bookLesson(w2sm); alice.bookLesson(w3sm);
        bob.bookLesson(w1sm);   bob.bookLesson(w2um);   bob.bookLesson(w3um);
        carol.bookLesson(w1sa); carol.bookLesson(w2sa); carol.bookLesson(w3sa); carol.bookLesson(w4sm);
        dave.bookLesson(w1se);  dave.bookLesson(w2se);  dave.bookLesson(w3sa);
        emma.bookLesson(w1ua);  emma.bookLesson(w2ua);  emma.bookLesson(w3ua);  emma.bookLesson(w4ua);
        frank.bookLesson(w1sm); frank.bookLesson(w1ua); frank.bookLesson(w2sm);
        grace.bookLesson(w1sa); grace.bookLesson(w2sa); grace.bookLesson(w3um);
        harry.bookLesson(w1sm); harry.bookLesson(w2ua); harry.bookLesson(w3sm);
        irene.bookLesson(w1sa); irene.bookLesson(w2um); irene.bookLesson(w3ua);
        jack.bookLesson(w1se);  jack.bookLesson(w2sa);  jack.bookLesson(w3um);

        // ── 21 Reviews ───────────────────────────────────────────────────────
        alice.writeReview(w1sm, 5, "Loved the morning Yoga - very calming!");
        alice.writeReview(w1um, 4, "Great Zumba session, instructor was energetic.");
        alice.writeReview(w2sm, 5, "Another amazing Yoga class.");
        bob.writeReview(w1sm,   4, "Good Yoga session, bit crowded.");
        bob.writeReview(w2um,   3, "Pilates was ok, not my favourite.");
        bob.writeReview(w3um,   5, "Best Zumba class I've attended!");
        carol.writeReview(w1sa, 4, "Box Fit was really intense - loved it.");
        carol.writeReview(w2sa, 5, "Body Blitz on Saturday afternoon was excellent.");
        carol.writeReview(w3sa, 3, "Aquacise was ok, instructor seemed tired.");
        dave.writeReview(w1se,  5, "Evening Aquacise was perfect after a long week.");
        dave.writeReview(w2se,  4, "Good Zumba session to end the day.");
        dave.writeReview(w3sa,  2, "Disappointing class this week.");
        emma.writeReview(w1ua,  5, "Body Blitz on Sunday was fantastic!");
        emma.writeReview(w2ua,  5, "Instructor pushed us hard, brilliant.");
        emma.writeReview(w3ua,  4, "Really good Yoga afternoon class.");
        frank.writeReview(w1sm, 3, "Decent Yoga, nothing special.");
        frank.writeReview(w1ua, 4, "Body Blitz was challenging but fun.");
        grace.writeReview(w1sa, 5, "Box Fit was super fun with a great group.");
        grace.writeReview(w2sa, 4, "Good session, slightly short warm-up.");
        harry.writeReview(w1sm, 4, "Enjoyed the Yoga despite being packed.");
        jack.writeReview(w1se,  5, "Fantastic evening Aquacise, will rebook!");

        return system;
    }

    // ── Exercise rotation arrays (8 values each, one per weekend) ────────────
    private static final ExerciseType[] SAT_MORNING   = {ExerciseType.YOGA, ExerciseType.PILATES, ExerciseType.YOGA, ExerciseType.ZUMBA, ExerciseType.YOGA, ExerciseType.PILATES, ExerciseType.YOGA, ExerciseType.ZUMBA};
    private static final ExerciseType[] SAT_AFTERNOON = {ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ, ExerciseType.AQUACISE, ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ, ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ, ExerciseType.AQUACISE};
    private static final ExerciseType[] SAT_EVENING   = {ExerciseType.AQUACISE, ExerciseType.ZUMBA, ExerciseType.BODY_BLITZ, ExerciseType.PILATES, ExerciseType.ZUMBA, ExerciseType.AQUACISE, ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ};
    private static final ExerciseType[] SUN_MORNING   = {ExerciseType.ZUMBA, ExerciseType.PILATES, ExerciseType.ZUMBA, ExerciseType.YOGA, ExerciseType.ZUMBA, ExerciseType.PILATES, ExerciseType.ZUMBA, ExerciseType.YOGA};
    private static final ExerciseType[] SUN_AFTERNOON = {ExerciseType.BODY_BLITZ, ExerciseType.BODY_BLITZ, ExerciseType.YOGA, ExerciseType.BODY_BLITZ, ExerciseType.PILATES, ExerciseType.BODY_BLITZ, ExerciseType.AQUACISE, ExerciseType.BODY_BLITZ};
    private static final ExerciseType[] SUN_EVENING   = {ExerciseType.PILATES, ExerciseType.BOX_FIT, ExerciseType.BOX_FIT, ExerciseType.AQUACISE, ExerciseType.BOX_FIT, ExerciseType.ZUMBA, ExerciseType.PILATES, ExerciseType.BOX_FIT};
}
