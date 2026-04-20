package com.example.smarthabittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthabittracker.data.AppDatabase;
import com.example.smarthabittracker.data.Goal;
import com.example.smarthabittracker.data.HabitRepository;
import com.example.smarthabittracker.ui.adapter.GoalAdapter;
import com.example.smarthabittracker.ui.viewmodel.GoalViewModel;
import com.example.smarthabittracker.ui.viewmodel.GoalViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class GoalsListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GoalAdapter adapter;
    private TextView emptyStateText;
    private GoalViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_list);
        
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Goals");
        }
        
        recyclerView = findViewById(R.id.recyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        FloatingActionButton fab = findViewById(R.id.fab);
        
        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        HabitRepository repository = new HabitRepository(database.goalDao(), database.habitDao());
        viewModel = new ViewModelProvider(this, new GoalViewModelFactory(repository)).get(GoalViewModel.class);
        
        adapter = new GoalAdapter(new ArrayList<>(), goal -> {
            Intent intent = new Intent(GoalsListActivity.this, GoalDetailActivity.class);
            intent.putExtra("goalId", goal.getId());
            startActivity(intent);
        }, goal -> {
            new AlertDialog.Builder(this)
                .setTitle("Delete Goal")
                .setMessage("Are you sure you want to delete this goal? All associated habits will also be deleted.")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteGoal(goal))
                .setNegativeButton("Cancel", null)
                .show();
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        viewModel.getAllGoals().observe(this, goals -> {
            if (goals != null && !goals.isEmpty()) {
                adapter.updateGoals(goals);
                recyclerView.setVisibility(View.VISIBLE);
                emptyStateText.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyStateText.setVisibility(View.VISIBLE);
            }
        });
        
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(GoalsListActivity.this, AddEditGoalActivity.class);
            startActivity(intent);
        });
    }
}

