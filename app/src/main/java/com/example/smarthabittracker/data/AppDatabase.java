package com.example.smarthabittracker.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
    entities = {Goal.class, Habit.class},
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GoalDao goalDao();
    public abstract HabitDao habitDao();
    
    private static volatile AppDatabase INSTANCE;
    
    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "habit_tracker_database"
                    )
                        // Schema changed (Goal/Habit fields). For now, reset local DB on upgrade.
                        .fallbackToDestructiveMigration()
                        .build();
                }
            }
        }
        return INSTANCE;
    }
}

