package com.example.smarthabittracker.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthabittracker.R;
import com.example.smarthabittracker.data.Habit;

import java.util.List;

public class TodoHabitAdapter extends RecyclerView.Adapter<TodoHabitAdapter.TodoHabitViewHolder> {
    private List<Habit> habits;
    private OnCompletionToggleListener completionToggleListener;

    public interface OnCompletionToggleListener {
        void onToggle(Habit habit, boolean isCompleted);
    }

    public TodoHabitAdapter(List<Habit> habits, OnCompletionToggleListener completionToggleListener) {
        this.habits = habits;
        this.completionToggleListener = completionToggleListener;
    }

    public void updateHabits(List<Habit> newHabits) {
        this.habits = newHabits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoHabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_habit, parent, false);
        return new TodoHabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoHabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.titleCheckBox.setText(habit.getTitle());
        holder.titleCheckBox.setOnCheckedChangeListener(null);
        holder.titleCheckBox.setChecked(habit.isCompleted());
        holder.titleCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (completionToggleListener != null) {
                completionToggleListener.onToggle(habit, isChecked);
            }
        });

        if (habit.getDescription() != null && !habit.getDescription().isEmpty()) {
            holder.descriptionText.setText(habit.getDescription());
            holder.descriptionText.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    static class TodoHabitViewHolder extends RecyclerView.ViewHolder {
        CheckBox titleCheckBox;
        TextView descriptionText;

        TodoHabitViewHolder(@NonNull View itemView) {
            super(itemView);
            titleCheckBox = itemView.findViewById(R.id.titleCheckBox);
            descriptionText = itemView.findViewById(R.id.descriptionText);
        }
    }
}

