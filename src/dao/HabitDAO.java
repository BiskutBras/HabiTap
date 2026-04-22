package dao;

import db.DB;
import model.Habit;
import model.Habit.Priority;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitDAO {

    public void insert(Habit h) {
        String sql = "INSERT INTO habits (name, description, due_date, priority, completed) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, h.getName());
            ps.setString(2, h.getDescription());
            ps.setDate(3, Date.valueOf(h.getDueDate()));
            ps.setString(4, h.getPriority().name());
            ps.setInt(5, h.isCompleted() ? 1 : 0);

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("DB insert failed", e);
        }
    }

    public List<Habit> findAll() {
        String sql = "SELECT id, name, description, due_date, priority, completed FROM habits ORDER BY id ASC";
        List<Habit> out = new ArrayList<Habit>();

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String desc = rs.getString("description");
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                Priority priority = Priority.valueOf(rs.getString("priority"));
                boolean completed = rs.getInt("completed") == 1;

                Habit h = new Habit(id, name, desc, dueDate, priority);
                if (completed) h.markCompleted(); else h.markIncomplete();
                out.add(h);
            }
        } catch (Exception e) {
            throw new RuntimeException("DB read failed", e);
        }
        return out;
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
        String sql = "SELECT id, name, description, due_date, priority, completed FROM habits WHERE id = ?";
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

                Habit h = new Habit(hid, name, desc, dueDate, priority);
                if (completed) h.markCompleted(); else h.markIncomplete();
                return h;
            }
        } catch (Exception e) {
            throw new RuntimeException("DB read failed", e);
        }
    }

    public boolean update(int id, String name, String description, LocalDate dueDate, Priority priority) {
        String sql = "UPDATE habits SET name = ?, description = ?, due_date = ?, priority = ? WHERE id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDate(3, Date.valueOf(dueDate));
            ps.setString(4, priority.name());
            ps.setInt(5, id);

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
