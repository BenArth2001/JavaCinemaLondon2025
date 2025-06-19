package dao;

import database.DatabaseConnection;
import model.Showing;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShowingDAO {

    public List<Showing> getAllShowings() throws SQLException {
        List<Showing> showings = new ArrayList<>();
        String query = "SELECT * FROM showings";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                showings.add(extractShowingFromResultSet(resultSet));
            }
        }

        return showings;
    }

    public List<Showing> getShowingsByMovie(int movieId) throws SQLException {
        List<Showing> showings = new ArrayList<>();
        String query = "SELECT * FROM showings WHERE movie_id = ? ORDER BY show_date, show_time";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    showings.add(extractShowingFromResultSet(resultSet));
                }
            }
        }

        return showings;
    }

    public List<Showing> getShowingsByTheater(int theaterId) throws SQLException {
        List<Showing> showings = new ArrayList<>();
        String query = "SELECT * FROM showings WHERE theater_id = ? ORDER BY show_date, show_time";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, theaterId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    showings.add(extractShowingFromResultSet(resultSet));
                }
            }
        }

        return showings;
    }

    public List<Showing> getShowingsByDate(Date date) throws SQLException {
        List<Showing> showings = new ArrayList<>();
        String query = "SELECT * FROM showings WHERE show_date = ? ORDER BY show_time";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, date);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    showings.add(extractShowingFromResultSet(resultSet));
                }
            }
        }

        return showings;
    }

    public Showing getShowingById(int showingId) throws SQLException {
        String query = "SELECT * FROM showings WHERE showing_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, showingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractShowingFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    public void addShowing(Showing showing) throws SQLException {
        String query = "INSERT INTO showings (movie_id, theater_id, show_date, show_time, base_price) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, showing.getMovieId());
            statement.setInt(2, showing.getTheaterId());
            statement.setDate(3, showing.getShowDate());
            statement.setTime(4, showing.getShowTime());
            statement.setBigDecimal(5, showing.getBasePrice());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    showing.setShowingId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateShowing(Showing showing) throws SQLException {
        String query = "UPDATE showings SET movie_id = ?, theater_id = ?, show_date = ?, " +
                "show_time = ?, base_price = ? WHERE showing_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, showing.getMovieId());
            statement.setInt(2, showing.getTheaterId());
            statement.setDate(3, showing.getShowDate());
            statement.setTime(4, showing.getShowTime());
            statement.setBigDecimal(5, showing.getBasePrice());
            statement.setInt(6, showing.getShowingId());

            statement.executeUpdate();
        }
    }

    public void deleteShowing(int showingId) throws SQLException {
        String query = "DELETE FROM showings WHERE showing_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, showingId);
            statement.executeUpdate();
        }
    }

    private Showing extractShowingFromResultSet(ResultSet resultSet) throws SQLException {
        int showingId = resultSet.getInt("showing_id");
        int movieId = resultSet.getInt("movie_id");
        int theaterId = resultSet.getInt("theater_id");
        Date showDate = resultSet.getDate("show_date");
        Time showTime = resultSet.getTime("show_time");
        BigDecimal basePrice = resultSet.getBigDecimal("base_price");

        return new Showing(showingId, movieId, theaterId, showDate, showTime, basePrice);
    }
}
