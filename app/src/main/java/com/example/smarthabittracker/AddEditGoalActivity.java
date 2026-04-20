package com.example.smarthabittracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.smarthabittracker.data.AppDatabase;
import com.example.smarthabittracker.data.Goal;
import com.example.smarthabittracker.data.HabitRepository;
import com.example.smarthabittracker.ui.viewmodel.GoalViewModel;
import com.example.smarthabittracker.ui.viewmodel.GoalViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditGoalActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText expectedEndDateEditText;
    private Spinner difficultySpinner;
    private Button saveButton;
    private GoalViewModel viewModel;
    private long goalId = -1;
    private long expectedEndAt = 0;
    private Goal editingGoal;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_goal);
        
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Goal");
        }
        
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        expectedEndDateEditText = findViewById(R.id.expectedEndDateEditText);
        difficultySpinner = findViewById(R.id.difficultySpinner);
        saveButton = findViewById(R.id.saveButton);

        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            new String[]{"Easy", "Medium", "Hard"}
        );
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);

        expectedEndDateEditText.setOnClickListener(v -> showDatePicker(expectedEndAt, millis -> {
            expectedEndAt = millis;
            expectedEndDateEditText.setText(formatDate(millis));
        }));
        
        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        HabitRepository repository = new HabitRepository(database.goalDao(), database.habitDao());
        viewModel = new ViewModelProvider(this, new GoalViewModelFactory(repository)).get(GoalViewModel.class);
        
        goalId = getIntent().getLongExtra("goalId", -1);
        
        if (goalId != -1) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Goal");
            }
            viewModel.getGoalById(goalId).observe(this, goal -> {
                if (goal != null) {
                    editingGoal = goal;
                    titleEditText.setText(goal.getTitle());
                    descriptionEditText.setText(goal.getDescription());
                    expectedEndAt = goal.getExpectedEndAt();
                    if (expectedEndAt > 0) {
                        expectedEndDateEditText.setText(formatDate(expectedEndAt));
                    }
                    int diff = goal.getDifficulty();
                    if (diff >= 1 && diff <= 3) {
                        difficultySpinner.setSelection(diff - 1);
                    }
                }
            });
        }
        
        Button saveBtn = findViewById(R.id.saveButton);
        if (goalId != -1) {
            saveBtn.setText("Update Goal");
        }
        
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String description = descriptionEditText.getText().toString().trim();
            int difficulty = difficultySpinner.getSelectedItemPosition() + 1;
            
            Goal goal;
            if (goalId != -1) {
                if (editingGoal == null) {
                    Toast.makeText(this, "Goal not loaded yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                goal = editingGoal;
                goal.setTitle(title);
                goal.setDescription(description);
                goal.setExpectedEndAt(expectedEndAt);
                goal.setDifficulty(difficulty);
                viewModel.updateGoal(goal);
            } else {
                goal = new Goal(title, description, expectedEndAt, difficulty);
                viewModel.insertGoal(goal);
            }
            
            finish();
        });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private interface DatePickedCallback {
        void onPicked(long millis);
    }

    private void showDatePicker(long preselectMillis, DatePickedCallback callback) {
        Calendar cal = Calendar.getInstance();
        if (preselectMillis > 0) {
            cal.setTimeInMillis(preselectMillis);
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(Calendar.YEAR, y);
            picked.set(Calendar.MONTH, m);
            picked.set(Calendar.DAY_OF_MONTH, d);
            picked.set(Calendar.HOUR_OF_DAY, 0);
            picked.set(Calendar.MINUTE, 0);
            picked.set(Calendar.SECOND, 0);
            picked.set(Calendar.MILLISECOND, 0);
            callback.onPicked(picked.getTimeInMillis());
        }, year, month, day);
        dialog.show();
    }

    private String formatDate(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(millis);
    }
}

