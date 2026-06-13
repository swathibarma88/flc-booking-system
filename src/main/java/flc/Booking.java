package flc;

import java.util.UUID;

public class Booking {
    private final String bookingId;
    private final Member member;
    private       Lesson lesson;

    public Booking(Member member, Lesson lesson) {
        this.bookingId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.member    = member;
        this.lesson    = lesson;
    }

    public String getBookingId() { return bookingId; }
    public Member getMember()    { return member; }
    public Lesson getLesson()    { return lesson; }
    void setLesson(Lesson l)     { this.lesson = l; }

    @Override
    public String toString() {
        return String.format("Booking[%s] %s -> %s", bookingId, member.getName(), lesson);
    }
}
