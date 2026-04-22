-- HabiTap schema additions (Goals + Calendar)
-- Run these in your `habitap` MySQL database.

-- 1) Users table (Must be created first so Goals can reference it)
CREATE TABLE IF NOT EXISTS users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(60) NOT NULL UNIQUE,
                                     email VARCHAR(120) NOT NULL,
                                     password_hash VARCHAR(128) NOT NULL
);

-- 2) Goals table (Updated with user_id for 1-to-Many with users)
CREATE TABLE IF NOT EXISTS goals (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     user_id INT NOT NULL,               -- Links goal to a specific user
                                     name VARCHAR(80) NOT NULL,
                                     color VARCHAR(16) NOT NULL,
                                     CONSTRAINT fk_goals_user
                                         FOREIGN KEY (user_id) REFERENCES users(id)
                                             ON DELETE CASCADE                 -- If a user is deleted, their goals are deleted
);

-- 3) Update habits table with user_id and goal_id
-- (Add user_id for 1-to-Many from Users to Habits)
-- (Add goal_id for nullable 1-to-Many from Goals to Habits)
ALTER TABLE habits
    ADD COLUMN user_id INT NOT NULL DEFAULT 1,
    ADD COLUMN goal_id INT NULL,
    ADD CONSTRAINT fk_habits_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE CASCADE,               -- If a user is deleted, their habits are deleted
    ADD CONSTRAINT fk_habits_goal
        FOREIGN KEY (goal_id) REFERENCES goals(id)
            ON DELETE SET NULL;               -- If a goal is deleted, the habit loses the goal but isn't deleted

-- 4) Calendar items table (events + tasks)
CREATE TABLE IF NOT EXISTS calendar_items (
                                              id INT AUTO_INCREMENT PRIMARY KEY,
                                              type VARCHAR(10) NOT NULL,          -- EVENT / TASK
                                              title VARCHAR(120) NOT NULL,
                                              item_date DATE NOT NULL,
                                              start_time TIME NULL,
                                              end_time TIME NULL,
                                              category VARCHAR(50) NULL,
                                              description VARCHAR(500) NULL,
                                              priority VARCHAR(10) NULL           -- LOW / MEDIUM / HIGH (TASK only)
);

