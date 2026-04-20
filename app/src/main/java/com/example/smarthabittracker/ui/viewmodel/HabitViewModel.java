package com.example.smarthabittracker.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.smarthabittracker.data.HabitRepository;
import com.example.smarthabittracker.data.Habit;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HabitViewModel extends ViewModel {
    private HabitRepository repository;
    private Executor executor;
    
    public HabitViewModel(HabitRepository repository) {
        this.repository = repository;
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    public LiveData<List<Habit>> getHabitsByGoalId(long goalId) {
        return repository.getHabitsByGoalId(goalId);
    }
    
    public LiveData<List<Habit>> getAllHabits() {
        return repository.getAllHabits();
    }
    
    public void insertHabit(Habit habit) {
        executor.execute(() -> repository.insertHabit(habit));
    }
    
    public void updateHabit(Habit habit) {
        executor.execute(() -> repository.updateHabit(habit));
    }
    
    public void deleteHabit(Habit habit) {
        executor.execute(() -> repository.deleteHabit(habit));
    }
    
    public LiveData<Habit> getHabitById(long habitId) {
        return repository.getHabitById(habitId);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
