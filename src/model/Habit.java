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

    public Habit(int id, String name, String description, LocalDate dueDate, Priority priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public boolean isCompleted() { return completed; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public void markCompleted() { this.completed = true; }
    public void markIncomplete() { this.completed = false; }
}
