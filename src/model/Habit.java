package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor         // to read from a database
public class Habit {

    public enum Frequency {
        daily, weekly, monthly
    }

    // instance variables
    private int id;
    private String name;
    @Setter(AccessLevel.NONE)
    private String description;
    private boolean completed;
    private Frequency frequency;
    private int streak;

    // for views
    private int goalId;
    private String goalName;
    private String goalColor;

    // to create new habits
    public Habit(String name, String description, Frequency frequency, int streak, int goalId) {
        this.name = name;
        this.description = (description == null) ? "" : description;
        this.frequency = frequency;
        this.streak = streak;
        this.goalId = goalId;
    }

    // handle null
    public void setDescription(String description) {
        this.description = (description == null) ? "" : description;
    }

    // helper method
    public void markCompleted() {
        this.completed = true;
    }

    public void markIncomplete() {
        this.completed = false;
    }
}
