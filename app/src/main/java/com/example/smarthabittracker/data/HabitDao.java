package com.example.smarthabittracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface HabitDao {
    @Query("SELECT * FROM habits WHERE goalId = :goalId ORDER BY createdAt DESC")
    LiveData<List<Habit>> getHabitsByGoalId(long goalId);
    
    @Query("SELECT * FROM habits WHERE id = :habitId")
    LiveData<Habit> getHabitById(long habitId);
    
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    LiveData<List<Habit>> getAllHabits();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHabit(Habit habit);
    
    @Update
    void updateHabit(Habit habit);
    
    @Delete
    void deleteHabit(Habit habit);
}

