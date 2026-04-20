package com.example.smarthabittracker.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.smarthabittracker.data.HabitRepository;

public class HabitViewModelFactory implements ViewModelProvider.Factory {
    private final HabitRepository repository;

    public HabitViewModelFactory(HabitRepository repository) {
        this.repository = repository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HabitViewModel.class)) {
            return (T) new HabitViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

