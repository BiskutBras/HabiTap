-- HabiTap schema additions (Goals + Calendar)
-- Run these in your `habitap` MySQL database.

-- 1) Goals table
CREATE TABLE IF NOT EXISTS goals (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL,
  color VARCHAR(16) NOT NULL
);

-- 2) Link habits -> goals (nullable)
ALTER TABLE habits
  ADD COLUMN goal_id INT NULL,
  ADD CONSTRAINT fk_habits_goal
    FOREIGN KEY (goal_id) REFERENCES goals(id)
    ON DELETE SET NULL;

-- 3) Calendar items table (events + tasks)
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

-- 4) Users table
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(60) NOT NULL UNIQUE,
  email VARCHAR(120) NOT NULL,
  password_hash VARCHAR(128) NOT NULL
);

