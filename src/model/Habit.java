package model;

import java.time.LocalDate;

public class Habit {

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    private int id;
    private String name;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private boolean completed;
    private Integer goalId; // nullable
    private String goalName; // nullable (joined for views)
    private String goalColor; // nullable (joined for views)

    public Habit(int id, String name, String description, LocalDate dueDate, Priority priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
        this.goalId = null;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public boolean isCompleted() { return completed; }
    public Integer getGoalId() { return goalId; }
    public String getGoalName() { return goalName; }
    public String getGoalColor() { return goalColor; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setGoalId(Integer goalId) { this.goalId = goalId; }
    public void setGoalName(String goalName) { this.goalName = goalName; }
    public void setGoalColor(String goalColor) { this.goalColor = goalColor; }

    public void markCompleted() { this.completed = true; }
    public void markIncomplete() { this.completed = false; }
}
