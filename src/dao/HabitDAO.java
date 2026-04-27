package dao;

import db.DB;
import model.Habit;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitDAO {

    // add new record to table
    public void insert(Habit habit) {
        String sql = "INSERT INTO habits ( name, description, completed, frequency, streak, goal_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, habit.getName());
            ps.setString(2, habit.getDescription());
            ps.setBoolean(3, habit.isCompleted());
            ps.setString(4, habit.getFrequency().toString());
            ps.setInt(5, habit.getStreak());
            ps.setInt(6, habit.getGoalId());

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("DB insert failed", e);
        }
    }

    // view all columns from table
    public List<Habit> findAll(int userId) {
        // 1. Changed LEFT JOIN to JOIN (INNER JOIN)
        // 2. Changed the WHERE clause to filter by g.user_id instead of h.user_id
        String sql = "SELECT h.id, h.name, h.description, h.completed, h.frequency, h.streak, h.goal_id, " +
                "g.name AS goal_name, g.color AS goal_color " +
                "FROM habits h " +
                "JOIN goals g ON h.goal_id = g.id " +
                "WHERE g.user_id = ? " +
                "ORDER BY h.id ASC";

        List<Habit> habitList = new ArrayList<>();

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    boolean completed = rs.getInt("completed") == 1;
                    Habit.Frequency frequency = Habit.Frequency.valueOf(rs.getString("frequency"));
                    int streak = rs.getInt("streak");

                    int goalId = (Integer) rs.getInt("goal_id");
                    String goalName = rs.getString("goal_name");
                    String goalColor = rs.getString("goal_color");

                    Habit h = new Habit(id, name, description, completed, frequency, streak, goalId, goalName, goalColor);
                    if (completed) h.markCompleted();
                    else h.markIncomplete();

                    habitList.add(h);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("DB read failed", e);
        }
        System.out.println(habitList);
        return habitList;
    }

    public boolean setCompleted(int id, boolean completed) {
        String sql = "UPDATE habits SET completed = ? WHERE id = ?";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, completed ? 1 : 0);
            ps.setInt(2, id);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new RuntimeException("DB update failed", e);
        }
    }

    public Habit findById(int id) {
        String sql = "SELECT h.id, h.name, h.description, h.due_date, h.priority, h.completed, h.goal_id, " +
                "g.name AS goal_name, g.color AS goal_color " +
                "FROM habits h LEFT JOIN goals g ON h.goal_id = g.id WHERE h.id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                int hid = rs.getInt("id");
                String name = rs.getString("name");
                String desc = rs.getString("description");
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                Priority priority = Priority.valueOf(rs.getString("priority"));
                boolean completed = rs.getInt("completed") == 1;
                Integer goalId = (Integer) rs.getObject("goal_id");
                String goalName = rs.getString("goal_name");
                String goalColor = rs.getString("goal_color");

                Habit h = new Habit(hid, name, desc, dueDate, priority);
                if (completed) h.markCompleted();
                else h.markIncomplete();
                h.setGoalId(goalId);
                h.setGoalName(goalName);
                h.setGoalColor(goalColor);
                return h;
            }
        } catch (Exception e) {
            throw new RuntimeException("DB read failed", e);
        }
    }

    public boolean update(int id, String name, String description, LocalDate dueDate, Priority priority, Integer goalId) {
        String sql = "UPDATE habits SET name = ?, description = ?, due_date = ?, priority = ?, goal_id = ? WHERE id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDate(3, Date.valueOf(dueDate));
            ps.setString(4, priority.name());
            if (goalId == null) ps.setNull(5, java.sql.Types.INTEGER);
            else ps.setInt(5, goalId);
            ps.setInt(6, id);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new RuntimeException("DB update failed", e);
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM habits WHERE id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new RuntimeException("DB delete failed", e);
        }
    }
}
