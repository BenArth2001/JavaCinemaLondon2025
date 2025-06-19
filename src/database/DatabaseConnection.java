package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Matches the IntelliJ JDBC configuration you provided
    private static final String URL = "jdbc:mysql://localhost:3306/movie_booking_system";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        // Simple connection approach
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
