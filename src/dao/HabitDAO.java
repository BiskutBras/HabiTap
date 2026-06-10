package dao;

import db.DB;
import model.Habit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitDAO {

    // add new row
    public void insert(Habit habit) {
        String sql = "INSERT INTO habits ( name, description, completed, frequency, streak, goal_id, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bindInsertParameters(ps, habit);

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("DB insert failed", e);
        }
    }

    // view all columns
    public List<Habit> findAll(int userId) {
        // 1. Changed LEFT JOIN to JOIN (INNER JOIN)
        // 2. Changed the WHERE clause to filter by g.user_id instead of h.user_id
        String sql = "SELECT h.id, h.name, h.description, h.completed, h.frequency, h.streak, h.goal_id, h.user_id, " +
                "g.name AS goal_name, g.color AS goal_color " +
                "FROM habits h " +
                "LEFT JOIN goals g ON h.goal_id = g.id " +
                "WHERE h.user_id = ? " +
                "ORDER BY h.id ASC";

        List<Habit> habitList = new ArrayList<>();

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Habit habit = mapRow(rs);
                    habitList.add(habit);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("DB read failed", e);
        }
        System.out.println(habitList);
        return habitList;
    }

    // mark habit completed
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

    // view habit from id
    public Habit findById(int id) {
        String sql = "SELECT h.id, h.name, h.description, h.completed, h.frequency, h.streak, h.goal_id, h.user_id, " +
                "g.name AS goal_name, g.color AS goal_color " +
                "FROM habits h LEFT JOIN goals g ON h.goal_id = g.id WHERE h.id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                // result set mapping
                return mapRow(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException("DB read failed", e);
        }
    }

    // update row
    public boolean update(Habit habit) {
        String sql = "UPDATE habits SET name = ?, description = ?, completed = ?, frequency = ?, streak = ?, goal_id = ?, user_id = ? WHERE id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bindUpdateParameters(ps, habit);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new RuntimeException("DB update failed", e);
        }
    }

    // delete row
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

    // helper method
    private Habit mapRow(ResultSet rs) throws SQLException {
        int habitId = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        boolean completed = rs.getInt("completed") == 1;
        Habit.Frequency frequency = Habit.Frequency.valueOf(rs.getString("frequency"));
        int streak = rs.getInt("streak");

        int goalId = rs.getInt("goal_id");
        String goalName = rs.getString("goal_name");
        String goalColor = rs.getString("goal_color");
        int userId = rs.getInt("user_id");

        return new Habit(habitId, name, description, completed, frequency, streak, goalId, goalName, goalColor, userId);
    }

    private void bindUpdateParameters(PreparedStatement ps, Habit habit) throws SQLException {
        bindInsertParameters(ps, habit);
        ps.setInt(8, habit.getId());
    }

    private void bindInsertParameters(PreparedStatement ps, Habit habit) throws SQLException {
        ps.setString(1, habit.getName());
        ps.setString(2, habit.getDescription());
        ps.setBoolean(3, habit.isCompleted());
        ps.setString(4, habit.getFrequency().toString());
        ps.setInt(5, habit.getStreak());
        // handle habits with no goal
        if (habit.getGoalId() == 0) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, habit.getGoalId());
        }
        ps.setInt(7, habit.getUserId());
    }
}
