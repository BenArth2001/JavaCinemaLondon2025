package service;

import dao.BookingDAO;
import dao.TicketDAO;
import dao.SeatDAO;
import dao.ShowingDAO;
import model.Booking;
import model.Seat;
import model.Showing;
import model.Ticket;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BookingService {
    private BookingDAO bookingDAO;
    private TicketDAO ticketDAO;
    private SeatDAO seatDAO;
    private ShowingDAO showingDAO;
    private DiscountService discountService;
    private ShowingService showingService;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.ticketDAO = new TicketDAO();
        this.seatDAO = new SeatDAO();
        this.showingDAO = new ShowingDAO();
        this.discountService = new DiscountService();
        this.showingService = new ShowingService();
    }

    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.getAllBookings();
    }

    public List<Booking> getBookingsByUser(int userId) throws SQLException {
        return bookingDAO.getBookingsByUser(userId);
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        return bookingDAO.getBookingById(bookingId);
    }

    public List<Ticket> getTicketsForBooking(int bookingId) throws SQLException {
        return ticketDAO.getTicketsByBooking(bookingId);
    }

    public int createBooking(int userId, int showingId, List<Integer> seatIds, Integer discountId) throws SQLException {
        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        Showing showing = showingDAO.getShowingById(showingId);

        if (showing == null) {
            throw new SQLException("Invalid showing ID");
        }

        for (Integer seatId : seatIds) {
            Seat seat = seatDAO.getSeatById(seatId);
            if (seat != null) {
                BigDecimal ticketPrice = showingService.getShowingPrice(showingId, seat.getSeatType());
                totalAmount = totalAmount.add(ticketPrice);
            }
        }

        // Apply discount if applicable
        if (discountId != null && discountId > 0) {
            totalAmount = discountService.calculateDiscountedPrice(totalAmount, discountId);
        }

        // Create booking
        Booking booking = new Booking(userId, showingId, new Timestamp(System.currentTimeMillis()),
                totalAmount, discountId);

        int bookingId = bookingDAO.addBooking(booking);

        if (bookingId > 0) {
            // Create tickets for each seat
            for (Integer seatId : seatIds) {
                Seat seat = seatDAO.getSeatById(seatId);
                if (seat != null) {
                    BigDecimal ticketPrice = showingService.getShowingPrice(showingId, seat.getSeatType());

                    // Apply discount proportionally to each ticket if applicable
                    if (discountId != null && discountId > 0) {
                        ticketPrice = discountService.calculateDiscountedPrice(ticketPrice, discountId);
                    }

                    Ticket ticket = new Ticket(bookingId, seatId, ticketPrice);
                    ticketDAO.addTicket(ticket);
                }
            }
        }

        return bookingId;
    }

    public void cancelBooking(int bookingId) throws SQLException {
        // First delete all tickets associated with this booking
        ticketDAO.deleteTicketsByBooking(bookingId);

        // Then delete the booking itself
        bookingDAO.deleteBooking(bookingId);
    }

    public List<Seat> getBookedSeatsForShowing(int showingId) throws SQLException {
        List<Seat> bookedSeats = new ArrayList<>();
        List<Booking> bookings = bookingDAO.getBookingsByShowing(showingId);

        for (Booking booking : bookings) {
            List<Ticket> tickets = ticketDAO.getTicketsByBooking(booking.getBookingId());
            for (Ticket ticket : tickets) {
                Seat seat = seatDAO.getSeatById(ticket.getSeatId());
                if (seat != null) {
                    bookedSeats.add(seat);
                }
            }
        }

        return bookedSeats;
    }

    public BigDecimal getBookingTotal(int bookingId) throws SQLException {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking != null) {
            return booking.getTotalAmount();
        }
        return BigDecimal.ZERO;
    }

    public List<Object[]> getBookingDetailsWithSeats(int bookingId) throws SQLException {
        List<Object[]> ticketDetails = new ArrayList<>();
        List<Ticket> tickets = ticketDAO.getTicketsByBooking(bookingId);

        for (Ticket ticket : tickets) {
            Seat seat = seatDAO.getSeatById(ticket.getSeatId());
            if (seat != null) {
                Object[] detail = { ticket, seat };
                ticketDetails.add(detail);
            }
        }

        return ticketDetails;
    }
}
