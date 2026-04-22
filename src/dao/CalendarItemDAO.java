package dao;

import db.DB;
import model.CalendarItem;
import model.CalendarItem.Priority;
import model.CalendarItem.Type;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarItemDAO {

    public int insertAndReturnId(CalendarItem item) {
        String sql = "INSERT INTO calendar_items (type, title, item_date, start_time, end_time, category, description, priority) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, item.getType().name());
            ps.setString(2, item.getTitle());
            ps.setDate(3, Date.valueOf(item.getDate()));

            if (item.getStartTime() == null) ps.setNull(4, java.sql.Types.TIME);
            else ps.setTime(4, Time.valueOf(item.getStartTime()));

            if (item.getEndTime() == null) ps.setNull(5, java.sql.Types.TIME);
            else ps.setTime(5, Time.valueOf(item.getEndTime()));

            if (item.getCategory() == null) ps.setNull(6, java.sql.Types.VARCHAR);
            else ps.setString(6, item.getCategory());

            if (item.getDescription() == null) ps.setNull(7, java.sql.Types.VARCHAR);
            else ps.setString(7, item.getDescription());

            if (item.getPriority() == null) ps.setNull(8, java.sql.Types.VARCHAR);
            else ps.setString(8, item.getPriority().name());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new RuntimeException("Failed to retrieve generated id");
        } catch (Exception e) {
            throw new RuntimeException("DB insert failed", e);
        }
    }

    public List<CalendarItem> findAll() {
        String sql = "SELECT id, type, title, item_date, start_time, end_time, category, description, priority " +
                "FROM calendar_items ORDER BY item_date ASC, start_time ASC, id ASC";
        List<CalendarItem> out = new ArrayList<CalendarItem>();

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                Type type = Type.valueOf(rs.getString("type"));
                String title = rs.getString("title");
                LocalDate date = rs.getDate("item_date").toLocalDate();

                CalendarItem item = new CalendarItem(id, type, title, date);

                Time st = rs.getTime("start_time");
                Time et = rs.getTime("end_time");
                if (st != null) item.setStartTime(st.toLocalTime());
                if (et != null) item.setEndTime(et.toLocalTime());

                item.setCategory(rs.getString("category"));
                item.setDescription(rs.getString("description"));

                String pr = rs.getString("priority");
                if (pr != null) item.setPriority(Priority.valueOf(pr));

                out.add(item);
            }
        } catch (Exception e) {
            throw new RuntimeException("DB read failed", e);
        }
        return out;
    }

    public boolean update(CalendarItem item) {
        String sql = "UPDATE calendar_items SET title=?, item_date=?, start_time=?, end_time=?, category=?, description=?, priority=? WHERE id=?";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getTitle());
            ps.setDate(2, Date.valueOf(item.getDate()));

            if (item.getStartTime() == null) ps.setNull(3, java.sql.Types.TIME);
            else ps.setTime(3, Time.valueOf(item.getStartTime()));

            if (item.getEndTime() == null) ps.setNull(4, java.sql.Types.TIME);
            else ps.setTime(4, Time.valueOf(item.getEndTime()));

            if (item.getCategory() == null) ps.setNull(5, java.sql.Types.VARCHAR);
            else ps.setString(5, item.getCategory());

            if (item.getDescription() == null) ps.setNull(6, java.sql.Types.VARCHAR);
            else ps.setString(6, item.getDescription());

            if (item.getPriority() == null) ps.setNull(7, java.sql.Types.VARCHAR);
            else ps.setString(7, item.getPriority().name());

            ps.setInt(8, item.getId());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new RuntimeException("DB update failed", e);
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM calendar_items WHERE id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new RuntimeException("DB delete failed", e);
        }
    }

    public CalendarItem findById(int id) {
        String sql = "SELECT id, type, title, item_date, start_time, end_time, category, description, priority " +
                "FROM calendar_items WHERE id = ?";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Type type = Type.valueOf(rs.getString("type"));
                String title = rs.getString("title");
                LocalDate date = rs.getDate("item_date").toLocalDate();

                CalendarItem item = new CalendarItem(id, type, title, date);

                Time st = rs.getTime("start_time");
                Time et = rs.getTime("end_time");
                if (st != null) item.setStartTime(st.toLocalTime());
                if (et != null) item.setEndTime(et.toLocalTime());

                item.setCategory(rs.getString("category"));
                item.setDescription(rs.getString("description"));

                String pr = rs.getString("priority");
                if (pr != null) item.setPriority(Priority.valueOf(pr));

                return item;
            }
        } catch (Exception e) {
            throw new RuntimeException("DB read failed", e);
        }
    }
}

