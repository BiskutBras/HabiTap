package com.example.smarthabittracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    LiveData<List<Goal>> getAllGoals();
    
    @Query("SELECT * FROM goals WHERE id = :goalId")
    LiveData<Goal> getGoalById(long goalId);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertGoal(Goal goal);
    
    @Update
    void updateGoal(Goal goal);
    
    @Delete
    void deleteGoal(Goal goal);
}

