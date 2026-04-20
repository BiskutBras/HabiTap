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
import com.example.smarthabittracker.data.Habit;
import com.example.smarthabittracker.data.HabitRepository;
import com.example.smarthabittracker.ui.viewmodel.HabitViewModel;
import com.example.smarthabittracker.ui.viewmodel.HabitViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditHabitActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private Spinner prioritySpinner;
    private Spinner repeatSpinner;
    private EditText streakEditText;
    private Button saveButton;
    private HabitViewModel viewModel;
    private long goalId;
    private long habitId = -1;
    private long startAt = 0;
    private long endAt = 0;
    private Habit editingHabit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_habit);
        
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Habit");
        }
        
        goalId = getIntent().getLongExtra("goalId", -1);
        habitId = getIntent().getLongExtra("habitId", -1);
        
        if (goalId == -1) {
            finish();
            return;
        }
        
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        repeatSpinner = findViewById(R.id.repeatSpinner);
        streakEditText = findViewById(R.id.streakEditText);
        saveButton = findViewById(R.id.saveButton);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            new String[]{"Low", "Medium", "High"}
        );
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);

        ArrayAdapter<String> repeatAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            new String[]{"daily", "weekly", "monthly"}
        );
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(repeatAdapter);

        startDateEditText.setOnClickListener(v -> showDatePicker(startAt, millis -> {
            startAt = millis;
            startDateEditText.setText(formatDate(millis));
        }));
        endDateEditText.setOnClickListener(v -> showDatePicker(endAt, millis -> {
            endAt = millis;
            endDateEditText.setText(formatDate(millis));
        }));
        
        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        HabitRepository repository = new HabitRepository(database.goalDao(), database.habitDao());
        viewModel = new ViewModelProvider(this, new HabitViewModelFactory(repository)).get(HabitViewModel.class);
        
        if (habitId != -1) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Habit");
            }
            saveButton.setText("Update Habit");
            viewModel.getHabitById(habitId).observe(this, habit -> {
                if (habit != null) {
                    editingHabit = habit;
                    titleEditText.setText(habit.getTitle());
                    descriptionEditText.setText(habit.getDescription());
                    startAt = habit.getStartAt();
                    endAt = habit.getEndAt();
                    if (startAt > 0) startDateEditText.setText(formatDate(startAt));
                    if (endAt > 0) endDateEditText.setText(formatDate(endAt));
                    int priority = habit.getPriority();
                    if (priority >= 1 && priority <= 3) prioritySpinner.setSelection(priority - 1);
                    String repeat = habit.getRepeat();
                    if (repeat != null) {
                        if (repeat.equalsIgnoreCase("daily")) repeatSpinner.setSelection(0);
                        else if (repeat.equalsIgnoreCase("weekly")) repeatSpinner.setSelection(1);
                        else if (repeat.equalsIgnoreCase("monthly")) repeatSpinner.setSelection(2);
                    }
                    streakEditText.setText(String.valueOf(habit.getStreakCount()));
                }
            });
        }
        
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String description = descriptionEditText.getText().toString().trim();
            
            String repeat = String.valueOf(repeatSpinner.getSelectedItem());
            int priority = prioritySpinner.getSelectedItemPosition() + 1;
            int streakCount = 0;
            try {
                String streakStr = streakEditText.getText().toString().trim();
                if (!streakStr.isEmpty()) {
                    streakCount = Integer.parseInt(streakStr);
                }
            } catch (NumberFormatException ignored) {
            }

            Habit habit;
            if (habitId != -1) {
                if (editingHabit == null) {
                    Toast.makeText(this, "Habit not loaded yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                habit = editingHabit;
                habit.setGoalId(goalId);
                habit.setTitle(title);
                habit.setDescription(description);
                habit.setStartAt(startAt);
                habit.setEndAt(endAt);
                habit.setPriority(priority);
                habit.setRepeat(repeat);
                habit.setStreakCount(streakCount);
                viewModel.updateHabit(habit);
            } else {
                habit = new Habit(goalId, title, description, startAt, endAt, priority, repeat, streakCount, false);
                viewModel.insertHabit(habit);
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

