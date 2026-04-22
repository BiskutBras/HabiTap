package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Goal {
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Status {
        ACTIVE, COMPLETED, CANCELLED
    }

    private int id;
    private String name;
    private String color; // hex like #2563eb
    private LocalDateTime createdDate;
    private LocalDateTime dueDate;
    private Priority priority;
    private Status status;
    private int userId;


    public Goal(int id, String name, String color, int userId, LocalDateTime createdDate, LocalDateTime dueDate, Priority priority, Status status) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.userId = userId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public int getUserId(){ return userId; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }

    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setStatus(Status status) { this.status = status; }
}
