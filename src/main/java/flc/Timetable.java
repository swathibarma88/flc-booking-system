package flc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Timetable {
    private final int          weekNumber;
    private final Day          day;
    private final List<Lesson> lessons = new ArrayList<>();

    public Timetable(int weekNumber, Day day) {
        this.weekNumber = weekNumber;
        this.day        = day;
    }

    public void addLesson(Lesson lesson) {
        if (lessons.size() >= 3)
            throw new IllegalStateException("A timetable can only hold 3 lessons per day.");
        lessons.add(lesson);
    }

    public List<Lesson> getLessons()              { return List.copyOf(lessons); }
    public List<Lesson> getLessonsBySlot(TimeSlot slot) {
        return lessons.stream().filter(l -> l.getTimeSlot() == slot).collect(Collectors.toList());
    }

    public int  getWeekNumber() { return weekNumber; }
    public Day  getDay()        { return day; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Week %d — %s ===%n", weekNumber, day));
        for (Lesson l : lessons) sb.append("  ").append(l).append("\n");
        return sb.toString();
    }
}
