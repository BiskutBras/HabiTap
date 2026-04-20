package com.example.smarthabittracker.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "habits",
    foreignKeys = {
        @ForeignKey(
            entity = Goal.class,
            parentColumns = {"id"},
            childColumns = {"goalId"},
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index(value = {"goalId"})}
)
public class Habit {
    @PrimaryKey(autoGenerate = true)
    private long id = 0;
    private long goalId;
    private String title; // Habit name
    private String description = "";
    private long createdAt;
    private long startAt;
    private long endAt;
    private int priority; // 1=Low, 2=Medium, 3=High
    private String repeat; // daily, weekly, monthly
    private int streakCount;
    private boolean isCompleted;

    public Habit() {
        this.createdAt = System.currentTimeMillis();
    }

    @Ignore
    public Habit(long goalId, String title, String description, long startAt, long endAt, int priority, String repeat, int streakCount, boolean isCompleted) {
        this.goalId = goalId;
        this.title = title;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
        this.startAt = startAt;
        this.endAt = endAt;
        this.priority = priority;
        this.repeat = repeat;
        this.streakCount = streakCount;
        this.isCompleted = isCompleted;
    }

    @Ignore
    public Habit(long id, long goalId, String title, String description, long createdAt, long startAt, long endAt, int priority, String repeat, int streakCount, boolean isCompleted) {
        this.id = id;
        this.goalId = goalId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.startAt = startAt;
        this.endAt = endAt;
        this.priority = priority;
        this.repeat = repeat;
        this.streakCount = streakCount;
        this.isCompleted = isCompleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGoalId() {
        return goalId;
    }

    public void setGoalId(long goalId) {
        this.goalId = goalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getStartAt() {
        return startAt;
    }

    public void setStartAt(long startAt) {
        this.startAt = startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

