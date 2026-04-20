package com.example.smarthabittracker.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthabittracker.R;
import com.example.smarthabittracker.data.Habit;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {
    private List<Habit> habits;
    private OnHabitClickListener clickListener;
    private OnHabitDeleteListener deleteListener;
    
    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
    }
    
    public interface OnHabitDeleteListener {
        void onHabitDelete(Habit habit);
    }
    
    public HabitAdapter(List<Habit> habits, OnHabitClickListener clickListener, OnHabitDeleteListener deleteListener) {
        this.habits = habits;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }
    
    public void updateHabits(List<Habit> newHabits) {
        this.habits = newHabits;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.titleText.setText(habit.getTitle());
        if (habit.getDescription() != null && !habit.getDescription().isEmpty()) {
            holder.descriptionText.setText(habit.getDescription());
            holder.descriptionText.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionText.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onHabitClick(habit);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onHabitDelete(habit);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return habits.size();
    }
    
    static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView descriptionText;
        TextView deleteButton;
        
        HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

