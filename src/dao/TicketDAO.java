package dao;

import database.DatabaseConnection;
import model.Ticket;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public List<Ticket> getAllTickets() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM tickets";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                tickets.add(extractTicketFromResultSet(resultSet));
            }
        }

        return tickets;
    }

    public List<Ticket> getTicketsByBooking(int bookingId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM tickets WHERE booking_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bookingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tickets.add(extractTicketFromResultSet(resultSet));
                }
            }
        }

        return tickets;
    }

    public Ticket getTicketById(int ticketId) throws SQLException {
        String query = "SELECT * FROM tickets WHERE ticket_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, ticketId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractTicketFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    public void addTicket(Ticket ticket) throws SQLException {
        String query = "INSERT INTO tickets (booking_id, seat_id, price) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, ticket.getBookingId());
            statement.setInt(2, ticket.getSeatId());
            statement.setBigDecimal(3, ticket.getPrice());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticket.setTicketId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateTicket(Ticket ticket) throws SQLException {
        String query = "UPDATE tickets SET booking_id = ?, seat_id = ?, price = ? WHERE ticket_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, ticket.getBookingId());
            statement.setInt(2, ticket.getSeatId());
            statement.setBigDecimal(3, ticket.getPrice());
            statement.setInt(4, ticket.getTicketId());

            statement.executeUpdate();
        }
    }

    public void deleteTicket(int ticketId) throws SQLException {
        String query = "DELETE FROM tickets WHERE ticket_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, ticketId);
            statement.executeUpdate();
        }
    }

    public void deleteTicketsByBooking(int bookingId) throws SQLException {
        String query = "DELETE FROM tickets WHERE booking_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bookingId);
            statement.executeUpdate();
        }
    }

    private Ticket extractTicketFromResultSet(ResultSet resultSet) throws SQLException {
        int ticketId = resultSet.getInt("ticket_id");
        int bookingId = resultSet.getInt("booking_id");
        int seatId = resultSet.getInt("seat_id");
        BigDecimal price = resultSet.getBigDecimal("price");

        return new Ticket(ticketId, bookingId, seatId, price);
    }
}
