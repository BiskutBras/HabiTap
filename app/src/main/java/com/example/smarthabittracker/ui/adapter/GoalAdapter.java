package com.example.smarthabittracker.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthabittracker.R;
import com.example.smarthabittracker.data.Goal;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private List<Goal> goals;
    private OnGoalClickListener clickListener;
    private OnGoalDeleteListener deleteListener;
    
    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
    }
    
    public interface OnGoalDeleteListener {
        void onGoalDelete(Goal goal);
    }
    
    public GoalAdapter(List<Goal> goals, OnGoalClickListener clickListener, OnGoalDeleteListener deleteListener) {
        this.goals = goals;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }
    
    public void updateGoals(List<Goal> newGoals) {
        this.goals = newGoals;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.titleText.setText(goal.getTitle());
        if (goal.getDescription() != null && !goal.getDescription().isEmpty()) {
            holder.descriptionText.setText(goal.getDescription());
            holder.descriptionText.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionText.setVisibility(View.GONE);
        }

        int percent = goal.getCompletionPercent();
        holder.progressBar.setProgress(percent);
        holder.progressText.setText(percent + "%");
        
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onGoalClick(goal);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onGoalDelete(goal);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return goals.size();
    }
    
    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView descriptionText;
        ProgressBar progressBar;
        TextView progressText;
        TextView deleteButton;
        
        GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            progressBar = itemView.findViewById(R.id.progressBar);
            progressText = itemView.findViewById(R.id.progressText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

