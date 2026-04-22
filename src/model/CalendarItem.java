package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarItem {

    public enum Type { EVENT, TASK }
    public enum Priority { LOW, MEDIUM, HIGH }

    private int id;
    private Type type;
    private String title;
    private LocalDate date;

    // event fields
    private LocalTime startTime; // nullable
    private LocalTime endTime;   // nullable
    private String category;     // nullable

    // shared
    private String description;  // nullable

    // task fields
    private Priority priority;   // nullable for EVENT

    public CalendarItem(int id, Type type, String title, LocalDate date) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.date = date;
    }

    public int getId() { return id; }
    public Type getType() { return type; }
    public String getTitle() { return title; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }

    public void setTitle(String title) { this.title = title; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(Priority priority) { this.priority = priority; }
}

