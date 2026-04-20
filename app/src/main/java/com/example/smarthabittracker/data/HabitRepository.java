package com.example.smarthabittracker.data;

import androidx.lifecycle.LiveData;

import java.util.List;

public class HabitRepository {
    private GoalDao goalDao;
    private HabitDao habitDao;
    
    public HabitRepository(GoalDao goalDao, HabitDao habitDao) {
        this.goalDao = goalDao;
        this.habitDao = habitDao;
    }
    
    // Goal operations
    public LiveData<List<Goal>> getAllGoals() {
        return goalDao.getAllGoals();
    }
    
    public LiveData<Goal> getGoalById(long goalId) {
        return goalDao.getGoalById(goalId);
    }
    
    public long insertGoal(Goal goal) {
        return goalDao.insertGoal(goal);
    }
    
    public void updateGoal(Goal goal) {
        goalDao.updateGoal(goal);
    }
    
    public void deleteGoal(Goal goal) {
        goalDao.deleteGoal(goal);
    }
    
    // Habit operations
    public LiveData<List<Habit>> getHabitsByGoalId(long goalId) {
        return habitDao.getHabitsByGoalId(goalId);
    }
    
    public LiveData<List<Habit>> getAllHabits() {
        return habitDao.getAllHabits();
    }
    
    public LiveData<Habit> getHabitById(long habitId) {
        return habitDao.getHabitById(habitId);
    }
    
    public long insertHabit(Habit habit) {
        return habitDao.insertHabit(habit);
    }
    
    public void updateHabit(Habit habit) {
        habitDao.updateHabit(habit);
    }
    
    public void deleteHabit(Habit habit) {
        habitDao.deleteHabit(habit);
    }
}

