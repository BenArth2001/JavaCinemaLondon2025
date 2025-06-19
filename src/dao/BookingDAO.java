package dao;

import database.DatabaseConnection;
import model.Booking;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                bookings.add(extractBookingFromResultSet(resultSet));
            }
        }

        return bookings;
    }

    public List<Booking> getBookingsByUser(int userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_date DESC";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(extractBookingFromResultSet(resultSet));
                }
            }
        }

        return bookings;
    }

    public List<Booking> getBookingsByShowing(int showingId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE showing_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, showingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(extractBookingFromResultSet(resultSet));
                }
            }
        }

        return bookings;
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        String query = "SELECT * FROM bookings WHERE booking_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bookingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractBookingFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    public int addBooking(Booking booking) throws SQLException {
        String query = "INSERT INTO bookings (user_id, showing_id, booking_date, total_amount, discount_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, booking.getUserId());
            statement.setInt(2, booking.getShowingId());

            if (booking.getBookingDate() != null) {
                statement.setTimestamp(3, booking.getBookingDate());
            } else {
                statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            }

            statement.setBigDecimal(4, booking.getTotalAmount());

            if (booking.getDiscountId() != null) {
                statement.setInt(5, booking.getDiscountId());
            } else {
                statement.setNull(5, Types.INTEGER);
            }

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int bookingId = generatedKeys.getInt(1);
                    booking.setBookingId(bookingId);
                    return bookingId;
                }
            }
        }

        return -1;
    }

    public void updateBooking(Booking booking) throws SQLException {
        String query = "UPDATE bookings SET user_id = ?, showing_id = ?, booking_date = ?, " +
                "total_amount = ?, discount_id = ? WHERE booking_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, booking.getUserId());
            statement.setInt(2, booking.getShowingId());
            statement.setTimestamp(3, booking.getBookingDate());
            statement.setBigDecimal(4, booking.getTotalAmount());

            if (booking.getDiscountId() != null) {
                statement.setInt(5, booking.getDiscountId());
            } else {
                statement.setNull(5, Types.INTEGER);
            }

            statement.setInt(6, booking.getBookingId());

            statement.executeUpdate();
        }
    }

    public void deleteBooking(int bookingId) throws SQLException {
        String query = "DELETE FROM bookings WHERE booking_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bookingId);
            statement.executeUpdate();
        }
    }

    private Booking extractBookingFromResultSet(ResultSet resultSet) throws SQLException {
        int bookingId = resultSet.getInt("booking_id");
        int userId = resultSet.getInt("user_id");
        int showingId = resultSet.getInt("showing_id");
        Timestamp bookingDate = resultSet.getTimestamp("booking_date");
        BigDecimal totalAmount = resultSet.getBigDecimal("total_amount");

        Integer discountId = resultSet.getInt("discount_id");
        if (resultSet.wasNull()) {
            discountId = null;
        }

        return new Booking(bookingId, userId, showingId, bookingDate, totalAmount, discountId);
    }
}
