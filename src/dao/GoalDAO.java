package dao;

import db.DB;
import model.Goal;
import model.Goal.Priority;
import model.Goal.Status;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {

    public int insertAndReturnId(Goal g) {
        String sql = "INSERT INTO goals (name, color, user_id, created_date, due_date, priority, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, g.getName());
            ps.setString(2, g.getColor());
            ps.setInt(3, g.getUserId());
            ps.setTimestamp(4, Timestamp.valueOf(g.getCreatedDate()));
            ps.setDate(5, g.getDueDate() != null ? Date.valueOf(g.getDueDate().toLocalDate()) : null);
            ps.setString(6, g.getPriority().name());
            ps.setString(7, g.getStatus().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new RuntimeException("Failed to retrieve generated goal id");
        } catch (Exception e) {
            throw new RuntimeException("DB insert failed", e);
        }
    }

    public List<Goal> findAll() {
        String sql = "SELECT id, name, color, user_id, created_date, due_date, priority, status FROM goals ORDER BY id ASC";
        List<Goal> out = new ArrayList<Goal>();

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDateTime createdDate = rs.getTimestamp("created_date") != null ?
                    rs.getTimestamp("created_date").toLocalDateTime() : LocalDateTime.now();
                LocalDate dueDate = rs.getDate("due_date") != null ?
                    rs.getDate("due_date").toLocalDate() : null;
                Priority priority = Priority.valueOf(rs.getString("priority") != null ?
                    rs.getString("priority") : "MEDIUM");
                Status status = Status.valueOf(rs.getString("status") != null ?
                    rs.getString("status") : "ACTIVE");

                out.add(new Goal(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("color"),
                        rs.getInt("user_id"),
                        createdDate,
                        dueDate.atStartOfDay(),
                        priority,
                        status
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("DB read failed", e);
        }
        return out;
    }
}
