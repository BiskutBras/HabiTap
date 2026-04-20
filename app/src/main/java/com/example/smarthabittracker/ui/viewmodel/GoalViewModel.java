package com.example.smarthabittracker.ui.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.smarthabittracker.data.HabitRepository;
import com.example.smarthabittracker.data.Goal;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GoalViewModel extends ViewModel {
    private HabitRepository repository;
    private Executor executor;
    private LiveData<List<Goal>> allGoals;
    
    public GoalViewModel(HabitRepository repository) {
        this.repository = repository;
        this.executor = Executors.newSingleThreadExecutor();
        this.allGoals = repository.getAllGoals();
    }
    
    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }
    
    public void insertGoal(Goal goal) {
        executor.execute(() -> repository.insertGoal(goal));
    }
    
    public void updateGoal(Goal goal) {
        executor.execute(() -> repository.updateGoal(goal));
    }
    
    public void deleteGoal(Goal goal) {
        executor.execute(() -> repository.deleteGoal(goal));
    }
    
    public LiveData<Goal> getGoalById(long goalId) {
        return repository.getGoalById(goalId);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
