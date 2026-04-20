package com.example.smarthabittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthabittracker.data.AppDatabase;
import com.example.smarthabittracker.data.Goal;
import com.example.smarthabittracker.data.Habit;
import com.example.smarthabittracker.data.HabitRepository;
import com.example.smarthabittracker.ui.adapter.HabitAdapter;
import com.example.smarthabittracker.ui.viewmodel.GoalViewModel;
import com.example.smarthabittracker.ui.viewmodel.GoalViewModelFactory;
import com.example.smarthabittracker.ui.viewmodel.HabitViewModel;
import com.example.smarthabittracker.ui.viewmodel.HabitViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class GoalDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private RecyclerView recyclerView;
    private HabitAdapter adapter;
    private TextView emptyStateText;
    private TextView calendarPlaceholder;
    private long goalId;
    private GoalViewModel goalViewModel;
    private HabitViewModel habitViewModel;
    private MaterialButtonToggleGroup viewModeToggleGroup;
    private MaterialButton btnTodoList;
    private Goal currentGoal;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
        
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Goal Details");
        }
        
        goalId = getIntent().getLongExtra("goalId", -1);
        if (goalId == -1) {
            finish();
            return;
        }
        
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        recyclerView = findViewById(R.id.recyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        calendarPlaceholder = findViewById(R.id.calendarPlaceholder);
        viewModeToggleGroup = findViewById(R.id.viewModeToggleGroup);
        btnTodoList = findViewById(R.id.btnTodoList);
        FloatingActionButton fab = findViewById(R.id.fab);
        
        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        HabitRepository repository = new HabitRepository(database.goalDao(), database.habitDao());
        goalViewModel = new ViewModelProvider(this, new GoalViewModelFactory(repository)).get(GoalViewModel.class);
        habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(repository)).get(HabitViewModel.class);
        
        goalViewModel.getGoalById(goalId).observe(this, goal -> {
            if (goal != null) {
                currentGoal = goal;
                titleTextView.setText(goal.getTitle());
                if (goal.getDescription() != null && !goal.getDescription().isEmpty()) {
                    descriptionTextView.setText(goal.getDescription());
                    descriptionTextView.setVisibility(View.VISIBLE);
                } else {
                    descriptionTextView.setVisibility(View.GONE);
                }
            }
        });
        
        adapter = new HabitAdapter(new ArrayList<>(), habit -> {
            Intent intent = new Intent(GoalDetailActivity.this, AddEditHabitActivity.class);
            intent.putExtra("goalId", goalId);
            intent.putExtra("habitId", habit.getId());
            startActivity(intent);
        }, habit -> {
            new AlertDialog.Builder(this)
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete this habit?")
                .setPositiveButton("Delete", (dialog, which) -> habitViewModel.deleteHabit(habit))
                .setNegativeButton("Cancel", null)
                .show();
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Default to list mode
        if (viewModeToggleGroup != null) {
            viewModeToggleGroup.check(R.id.btnList);
            viewModeToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (!isChecked) return;
                boolean showList = checkedId == R.id.btnList;
                recyclerView.setVisibility(showList ? View.VISIBLE : View.GONE);
                emptyStateText.setVisibility(showList ? emptyStateText.getVisibility() : View.GONE);
                calendarPlaceholder.setVisibility(showList ? View.GONE : View.VISIBLE);
            });
        }

        if (btnTodoList != null) {
            btnTodoList.setOnClickListener(v -> {
                Intent intent = new Intent(GoalDetailActivity.this, TodoListActivity.class);
                intent.putExtra("goalId", goalId);
                startActivity(intent);
            });
        }
        
        habitViewModel.getHabitsByGoalId(goalId).observe(this, habits -> {
            if (currentGoal != null) {
                int total = habits == null ? 0 : habits.size();
                int completed = 0;
                if (habits != null) {
                    for (Habit h : habits) {
                        if (h != null && h.isCompleted()) completed++;
                    }
                }
                int percent = total == 0 ? 0 : (int) Math.round((completed * 100.0) / total);
                if (currentGoal.getCompletionPercent() != percent) {
                    currentGoal.setCompletionPercent(percent);
                    goalViewModel.updateGoal(currentGoal);
                }
            }

            if (habits != null && !habits.isEmpty()) {
                adapter.updateHabits(habits);
                if (viewModeToggleGroup != null && viewModeToggleGroup.getCheckedButtonId() == R.id.btnCalendar) {
                    recyclerView.setVisibility(View.GONE);
                    emptyStateText.setVisibility(View.GONE);
                    calendarPlaceholder.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyStateText.setVisibility(View.GONE);
                    calendarPlaceholder.setVisibility(View.GONE);
                }
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyStateText.setVisibility(View.VISIBLE);
                calendarPlaceholder.setVisibility(View.GONE);
            }
        });
        
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(GoalDetailActivity.this, AddEditHabitActivity.class);
            intent.putExtra("goalId", goalId);
            startActivity(intent);
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_goal_detail, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editGoal) {
            Intent intent = new Intent(this, AddEditGoalActivity.class);
            intent.putExtra("goalId", goalId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

