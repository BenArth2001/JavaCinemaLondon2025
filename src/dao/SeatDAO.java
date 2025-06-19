package dao;

import database.DatabaseConnection;
import model.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    public List<Seat> getAllSeats() throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM seats";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                seats.add(extractSeatFromResultSet(resultSet));
            }
        }

        return seats;
    }

    public List<Seat> getSeatsByTheater(int theaterId) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM seats WHERE theater_id = ? ORDER BY seat_row, seat_number";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, theaterId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    seats.add(extractSeatFromResultSet(resultSet));
                }
            }
        }

        return seats;
    }

    public List<Seat> getAvailableSeatsForShowing(int showingId) throws SQLException {
        List<Seat> availableSeats = new ArrayList<>();
        String query = "SELECT s.* FROM seats s " +
                "JOIN theaters t ON s.theater_id = t.theater_id " +
                "JOIN showings sh ON t.theater_id = sh.theater_id " +
                "WHERE sh.showing_id = ? " +
                "AND s.seat_id NOT IN " +
                "(SELECT t.seat_id FROM tickets t " +
                "JOIN bookings b ON t.booking_id = b.booking_id " +
                "WHERE b.showing_id = ?) " +
                "ORDER BY s.seat_row, s.seat_number";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, showingId);
            statement.setInt(2, showingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    availableSeats.add(extractSeatFromResultSet(resultSet));
                }
            }
        }

        return availableSeats;
    }

    public Seat getSeatById(int seatId) throws SQLException {
        String query = "SELECT * FROM seats WHERE seat_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, seatId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractSeatFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    public void addSeat(Seat seat) throws SQLException {
        String query = "INSERT INTO seats (theater_id, seat_row, seat_number, seat_type) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, seat.getTheaterId());
            statement.setString(2, String.valueOf(seat.getSeatRow()));
            statement.setInt(3, seat.getSeatNumber());
            statement.setString(4, seat.getSeatType());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    seat.setSeatId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateSeat(Seat seat) throws SQLException {
        String query = "UPDATE seats SET theater_id = ?, seat_row = ?, seat_number = ?, seat_type = ? WHERE seat_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, seat.getTheaterId());
            statement.setString(2, String.valueOf(seat.getSeatRow()));
            statement.setInt(3, seat.getSeatNumber());
            statement.setString(4, seat.getSeatType());
            statement.setInt(5, seat.getSeatId());

            statement.executeUpdate();
        }
    }

    private Seat extractSeatFromResultSet(ResultSet resultSet) throws SQLException {
        int seatId = resultSet.getInt("seat_id");
        int theaterId = resultSet.getInt("theater_id");
        char seatRow = resultSet.getString("seat_row").charAt(0);
        int seatNumber = resultSet.getInt("seat_number");
        String seatType = resultSet.getString("seat_type");

        return new Seat(seatId, theaterId, seatRow, seatNumber, seatType);
    }
}
