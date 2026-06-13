package flc;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private final String        memberId;
    private final String        name;
    private final List<Booking> bookings = new ArrayList<>();
    private final List<Review>  reviews  = new ArrayList<>();

    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name     = name;
    }

    public Booking bookLesson(Lesson lesson) {
        if (!lesson.hasSpace())
            throw new IllegalStateException("Lesson is full: " + lesson.getLessonId());
        if (hasTimeConflict(lesson))
            throw new IllegalStateException("Time conflict at " + lesson.getTimeSlot()
                    + " on " + lesson.getDay() + " week " + lesson.getWeekNumber());
        Booking b = new Booking(this, lesson);
        bookings.add(b);
        lesson.addBooking(b);
        return b;
    }

    public Booking changeBooking(Booking oldBooking, Lesson newLesson) {
        if (!bookings.contains(oldBooking))
            throw new IllegalArgumentException("Booking not found for this member.");
        if (!newLesson.hasSpace())
            throw new IllegalStateException("New lesson is full: " + newLesson.getLessonId());

        // Temporarily remove old booking so conflict check doesn't fire on the same slot
        bookings.remove(oldBooking);
        oldBooking.getLesson().removeBooking(oldBooking);

        if (hasTimeConflict(newLesson)) {
            // Rollback — restore original state
            bookings.add(oldBooking);
            oldBooking.getLesson().addBooking(oldBooking);
            throw new IllegalStateException("Time conflict with new lesson at " + newLesson.getTimeSlot());
        }

        Booking newBooking = new Booking(this, newLesson);
        bookings.add(newBooking);
        newLesson.addBooking(newBooking);
        return newBooking;
    }

    public void cancelBooking(Booking booking) {
        if (!bookings.contains(booking))
            throw new IllegalArgumentException("Booking not found for this member.");
        bookings.remove(booking);
        booking.getLesson().removeBooking(booking);
    }

    public boolean hasTimeConflict(Lesson lesson) {
        return bookings.stream().map(Booking::getLesson)
                .anyMatch(l -> l.getDay()       == lesson.getDay()
                        && l.getWeekNumber() == lesson.getWeekNumber()
                        && l.getTimeSlot()   == lesson.getTimeSlot());
    }

    public Review writeReview(Lesson lesson, int rating, String comment) {
        boolean attended = bookings.stream().map(Booking::getLesson)
                .anyMatch(l -> l.equals(lesson));
        if (!attended)
            throw new IllegalStateException(name + " has not attended lesson " + lesson.getLessonId());
        Review r = new Review(this, lesson, rating, comment);
        reviews.add(r);
        lesson.addReview(r);
        return r;
    }

    public String        getMemberId() { return memberId; }
    public String        getName()     { return name; }
    public List<Booking> getBookings() { return List.copyOf(bookings); }
    public List<Review>  getReviews()  { return List.copyOf(reviews); }

    @Override
    public String toString() {
        return String.format("Member[%s] %s (%d booking(s))", memberId, name, bookings.size());
    }
}
