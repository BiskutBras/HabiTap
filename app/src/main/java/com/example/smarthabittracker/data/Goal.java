package com.example.smarthabittracker.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    private long id = 0;
    private String title; // Goal name
    private String description = "";
    private long createdAt;
    private long expectedEndAt;
    private int difficulty; // 1=Easy, 2=Medium, 3=Hard
    private int completionPercent; // 0..100

    public Goal() {
        this.createdAt = System.currentTimeMillis();
    }

    @Ignore
    public Goal(String title, String description, long expectedEndAt, int difficulty) {
        this.title = title;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
        this.expectedEndAt = expectedEndAt;
        this.difficulty = difficulty;
        this.completionPercent = 0;
    }

    @Ignore
    public Goal(long id, String title, String description, long createdAt, long expectedEndAt, int difficulty, int completionPercent) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.expectedEndAt = expectedEndAt;
        this.difficulty = difficulty;
        this.completionPercent = completionPercent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getExpectedEndAt() {
        return expectedEndAt;
    }

    public void setExpectedEndAt(long expectedEndAt) {
        this.expectedEndAt = expectedEndAt;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getCompletionPercent() {
        return completionPercent;
    }

    public void setCompletionPercent(int completionPercent) {
        if (completionPercent < 0) completionPercent = 0;
        if (completionPercent > 100) completionPercent = 100;
        this.completionPercent = completionPercent;
    }
}

