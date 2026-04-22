package dao;

import db.DB;
import model.Goal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {

    public int insertAndReturnId(Goal g) {
        String sql = "INSERT INTO goals (name, color, user_id) VALUES (?, ?, ?)";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, g.getName());
            ps.setString(2, g.getColor());
            ps.setInt(3, g.getUserId());
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
        String sql = "SELECT id, name, color, user_id FROM goals ORDER BY id ASC";
        List<Goal> out = new ArrayList<Goal>();

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new Goal(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("color"),
                        rs.getInt("user_id")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("DB read failed", e);
        }
        return out;
    }
}

