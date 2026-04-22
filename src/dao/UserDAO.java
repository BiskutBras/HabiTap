package dao;

import db.DB;
import model.AuthUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static AuthUser findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, email FROM users WHERE username = ?";
        return getAuthUser(username, sql);
    }

    public static AuthUser findByEmail(String email) throws SQLException {
        String sql = "SELECT id, username, email FROM users WHERE email = ?";
        return getAuthUser(email, sql);
    }

    private static AuthUser getAuthUser(String loginInput, String sql) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, loginInput);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    return new AuthUser(id, username, email);
                }
            }
        }
        return null;
    }

    public static boolean createUser(String username, String email, String password) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        String hash = hashPassword(password);
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hash);
            int rows = ps.executeUpdate();
            return rows == 1;
        }
    }

    public static boolean validateCredentials(String username, String password) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password_hash");
                    return stored.equals(hashPassword(password));
                }
            }
        }
        return false;
    }

    private static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }


}

