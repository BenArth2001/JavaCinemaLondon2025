package dao;

import database.DatabaseConnection;
import model.Theater;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TheaterDAO {

    public List<Theater> getAllTheaters() throws SQLException {
        List<Theater> theaters = new ArrayList<>();
        String query = "SELECT * FROM theaters";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                theaters.add(extractTheaterFromResultSet(resultSet));
            }
        }

        return theaters;
    }

    public Theater getTheaterById(int theaterId) throws SQLException {
        String query = "SELECT * FROM theaters WHERE theater_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, theaterId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractTheaterFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    public void addTheater(Theater theater) throws SQLException {
        String query = "INSERT INTO theaters (theater_name, location, capacity) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, theater.getTheaterName());
            statement.setString(2, theater.getLocation());
            statement.setInt(3, theater.getCapacity());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    theater.setTheaterId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateTheater(Theater theater) throws SQLException {
        String query = "UPDATE theaters SET theater_name = ?, location = ?, capacity = ? WHERE theater_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, theater.getTheaterName());
            statement.setString(2, theater.getLocation());
            statement.setInt(3, theater.getCapacity());
            statement.setInt(4, theater.getTheaterId());

            statement.executeUpdate();
        }
    }

    public void deleteTheater(int theaterId) throws SQLException {
        String query = "DELETE FROM theaters WHERE theater_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, theaterId);
            statement.executeUpdate();
        }
    }

    private Theater extractTheaterFromResultSet(ResultSet resultSet) throws SQLException {
        int theaterId = resultSet.getInt("theater_id");
        String theaterName = resultSet.getString("theater_name");
        String location = resultSet.getString("location");
        int capacity = resultSet.getInt("capacity");

        return new Theater(theaterId, theaterName, location, capacity);
    }
}
