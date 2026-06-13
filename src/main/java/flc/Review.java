package flc;

public class Review {
    private final Member member;
    private final Lesson lesson;
    private final int    rating;
    private final String comment;

    public Review(Member member, Lesson lesson, int rating, String comment) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        this.member  = member;
        this.lesson  = lesson;
        this.rating  = rating;
        this.comment = comment;
    }

    public Member getMember()  { return member;  }
    public Lesson getLesson()  { return lesson;  }
    public int    getRating()  { return rating;  }
    public String getComment() { return comment; }

    public String ratingLabel() {
        return switch (rating) {
            case 1 -> "Very dissatisfied";
            case 2 -> "Dissatisfied";
            case 3 -> "Ok";
            case 4 -> "Satisfied";
            case 5 -> "Very satisfied";
            default -> "Unknown";
        };
    }

    @Override
    public String toString() {
        return String.format("Review by %s for %s: %d/5 (%s) - \"%s\"",
                member.getName(), lesson.getExerciseType().getDisplayName(),
                rating, ratingLabel(), comment);
    }
}
