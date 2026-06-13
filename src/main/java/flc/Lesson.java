package flc;

import java.util.ArrayList;
import java.util.List;

public class Lesson {

    public static final int CAPACITY = 4;

    private final String       lessonId;
    private final ExerciseType exerciseType;
    private final TimeSlot     timeSlot;
    private final Day          day;
    private final int          weekNumber;

    private final List<Booking> bookings = new ArrayList<>();
    private final List<Review>  reviews  = new ArrayList<>();

    public Lesson(String lessonId, ExerciseType exerciseType,
                  TimeSlot timeSlot, Day day, int weekNumber) {
        this.lessonId     = lessonId;
        this.exerciseType = exerciseType;
        this.timeSlot     = timeSlot;
        this.day          = day;
        this.weekNumber   = weekNumber;
    }

    public boolean hasSpace()        { return bookings.size() < CAPACITY; }
    public int     availableSpaces() { return CAPACITY - bookings.size(); }
    public int     getEnrolledCount(){ return bookings.size(); }

    void addBooking(Booking b) {
        if (!hasSpace()) throw new IllegalStateException("Lesson " + lessonId + " is full.");
        bookings.add(b);
    }

    void removeBooking(Booking b) { bookings.remove(b); }

    public List<Booking> getBookings() { return List.copyOf(bookings); }

    public void addReview(Review r)   { reviews.add(r); }
    public List<Review> getReviews()  { return List.copyOf(reviews); }

    public double getAverageRating() {
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public double getTotalIncome()    { return bookings.size() * getPrice(); }
    public String       getLessonId()     { return lessonId;     }
    public ExerciseType getExerciseType() { return exerciseType; }
    public TimeSlot     getTimeSlot()     { return timeSlot;     }
    public Day          getDay()          { return day;          }
    public int          getWeekNumber()   { return weekNumber;   }
    public double       getPrice()        { return exerciseType.getPrice(); }

    @Override
    public String toString() {
        return String.format("[%s] Week %d %s %s - %s (£%.2f, %d/%d spaces)",
                lessonId, weekNumber, day, timeSlot.getLabel(),
                exerciseType.getDisplayName(), getPrice(), getEnrolledCount(), CAPACITY);
    }
}
