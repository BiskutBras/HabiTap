package com.example.smarthabittracker.data;

import java.util.List;

public class GoalWithHabits {
    private Goal goal;
    private List<Habit> habits;

    public GoalWithHabits(Goal goal, List<Habit> habits) {
        this.goal = goal;
        this.habits = habits;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public List<Habit> getHabits() {
        return habits;
    }

    public void setHabits(List<Habit> habits) {
        this.habits = habits;
    }
}

