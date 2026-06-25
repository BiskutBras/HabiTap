package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    // Change these based on your XAMPP settings
    private static final String URL =
            "jdbc:mysql://localhost:3306/habitap?useSSL=false&serverTimezone=UTC"; // jdbc:mysql://localhost:3306/s72430_habitap
    private static final String DRIVERNAME = "com.mysql.cj.jdbc.Driver";
    private static final String USERNAME = "root"; // s72430
    private static final String PASSWORD = ""; // aKwjyLcvhPfB

    static {
        try {
            // MySQL Connector/J 8
            Class.forName(DRIVERNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Add connector JAR to WEB-INF/lib.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
