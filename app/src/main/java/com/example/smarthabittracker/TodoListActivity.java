package com.example.smarthabittracker;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthabittracker.data.AppDatabase;
import com.example.smarthabittracker.data.HabitRepository;
import com.example.smarthabittracker.ui.adapter.TodoHabitAdapter;
import com.example.smarthabittracker.ui.viewmodel.HabitViewModel;
import com.example.smarthabittracker.ui.viewmodel.HabitViewModelFactory;

import java.util.ArrayList;

public class TodoListActivity extends AppCompatActivity {
    private long goalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("To Do List");
        }

        goalId = getIntent().getLongExtra("goalId", -1);
        if (goalId == -1) {
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        TextView emptyStateText = findViewById(R.id.emptyStateText);

        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        HabitRepository repository = new HabitRepository(database.goalDao(), database.habitDao());
        HabitViewModel habitViewModel =
            new ViewModelProvider(this, new HabitViewModelFactory(repository)).get(HabitViewModel.class);

        TodoHabitAdapter adapter = new TodoHabitAdapter(new ArrayList<>(), (habit, isCompleted) -> {
            habit.setCompleted(isCompleted);
            habitViewModel.updateHabit(habit);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        habitViewModel.getHabitsByGoalId(goalId).observe(this, habits -> {
            if (habits != null && !habits.isEmpty()) {
                adapter.updateHabits(habits);
                recyclerView.setVisibility(View.VISIBLE);
                emptyStateText.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyStateText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

