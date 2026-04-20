package com.example.smarthabittracker.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.smarthabittracker.data.HabitRepository;

public class GoalViewModelFactory implements ViewModelProvider.Factory {
    private final HabitRepository repository;

    public GoalViewModelFactory(HabitRepository repository) {
        this.repository = repository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GoalViewModel.class)) {
            return (T) new GoalViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

