package service;

import model.*;
import model.report.*;
import dao.*;
import database.DatabaseConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

public class ReportService {
    private BookingDAO bookingDAO;
    private TicketDAO ticketDAO;
    private MovieDAO movieDAO;
    private TheaterDAO theaterDAO;
    private ShowingDAO showingDAO;
    private DiscountTypeDAO discountTypeDAO;

    public ReportService() {
        this.bookingDAO = new BookingDAO();
        this.ticketDAO = new TicketDAO();
        this.movieDAO = new MovieDAO();
        this.theaterDAO = new TheaterDAO();
        this.showingDAO = new ShowingDAO();
        this.discountTypeDAO = new DiscountTypeDAO();
    }

    public SalesReport generateSalesReport(java.sql.Date fromDate, java.sql.Date toDate) throws SQLException {
        SalesReport report = new SalesReport();
        List<SalesReportEntry> entries = new ArrayList<>();

        String query = "SELECT b.booking_id, b.booking_date, b.total_amount, b.showing_id, " +
                "COUNT(t.ticket_id) as ticket_count " +
                "FROM bookings b " +
                "JOIN tickets t ON b.booking_id = t.booking_id " +
                "WHERE b.booking_date BETWEEN ? AND ? " +
                "GROUP BY b.booking_id " +
                "ORDER BY b.booking_date DESC";

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalTickets = 0;
        int bookingCount = 0;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, fromDate);
            statement.setDate(2, toDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int bookingId = resultSet.getInt("booking_id");
                    Timestamp bookingDate = resultSet.getTimestamp("booking_date");
                    BigDecimal amount = resultSet.getBigDecimal("total_amount");
                    int showingId = resultSet.getInt("showing_id");
                    int ticketCount = resultSet.getInt("ticket_count");

                    // Get movie and theater info
                    Showing showing = showingDAO.getShowingById(showingId);
                    Movie movie = movieDAO.getMovieById(showing.getMovieId());
                    Theater theater = theaterDAO.getTheaterById(showing.getTheaterId());

                    SalesReportEntry entry = new SalesReportEntry();
                    entry.setDate(bookingDate);
                    entry.setMovieTitle(movie != null ? movie.getTitle() : "Unknown");
                    entry.setTheaterName(theater != null ? theater.getTheaterName() : "Unknown");
                    entry.setTicketsSold(ticketCount);
                    entry.setRevenue(amount);

                    entries.add(entry);

                    totalRevenue = totalRevenue.add(amount);
                    totalTickets += ticketCount;
                    bookingCount++;
                }
            }
        }

        report.setEntries(entries);
        report.setTotalRevenue(totalRevenue);
        report.setTicketsSold(totalTickets);
        report.setBookingCount(bookingCount);

        // Calculate average ticket price
        if (totalTickets > 0) {
            report.setAverageTicketPrice(totalRevenue.divide(new BigDecimal(totalTickets), 2, RoundingMode.HALF_UP));
        } else {
            report.setAverageTicketPrice(BigDecimal.ZERO);
        }

        return report;
    }

    public MoviePopularityReport generateMoviePopularityReport(java.sql.Date fromDate, java.sql.Date toDate)
            throws SQLException {
        MoviePopularityReport report = new MoviePopularityReport();
        Map<Integer, MoviePopularityEntry> movieStats = new HashMap<>();

        String query = "SELECT s.movie_id, COUNT(b.booking_id) as booking_count, " +
                "COUNT(t.ticket_id) as ticket_count, SUM(b.total_amount) as revenue " +
                "FROM showings s " +
                "JOIN bookings b ON s.showing_id = b.showing_id " +
                "JOIN tickets t ON b.booking_id = t.booking_id " +
                "WHERE b.booking_date BETWEEN ? AND ? " +
                "GROUP BY s.movie_id";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, fromDate);
            statement.setDate(2, toDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int movieId = resultSet.getInt("movie_id");
                    int bookingCount = resultSet.getInt("booking_count");
                    int ticketCount = resultSet.getInt("ticket_count");
                    BigDecimal revenue = resultSet.getBigDecimal("revenue");

                    Movie movie = movieDAO.getMovieById(movieId);
                    if (movie != null) {
                        MoviePopularityEntry entry = new MoviePopularityEntry();
                        entry.setMovieId(movieId);
                        entry.setMovieTitle(movie.getTitle());
                        entry.setTicketsSold(ticketCount);
                        entry.setRevenue(revenue);

                        // Count showings for this movie in the date range
                        int showingCount = countShowingsForMovie(movieId, fromDate, toDate);
                        entry.setShowingCount(showingCount);

                        // Calculate average occupancy
                        double occupancy = calculateAverageOccupancyForMovie(movieId, fromDate, toDate);
                        entry.setAverageOccupancy(occupancy);

                        movieStats.put(movieId, entry);
                    }
                }
            }
        }

        // Sort entries by tickets sold in descending order
        List<MoviePopularityEntry> entries = new ArrayList<>(movieStats.values());
        entries.sort((e1, e2) -> Integer.compare(e2.getTicketsSold(), e1.getTicketsSold()));

        report.setEntries(entries);
        return report;
    }

    public TheaterUtilizationReport generateTheaterUtilizationReport(java.sql.Date fromDate, java.sql.Date toDate)
            throws SQLException {
        TheaterUtilizationReport report = new TheaterUtilizationReport();
        Map<Integer, TheaterUtilizationEntry> theaterStats = new HashMap<>();

        // Get all theaters
        List<Theater> theaters = theaterDAO.getAllTheaters();

        for (Theater theater : theaters) {
            TheaterUtilizationEntry entry = new TheaterUtilizationEntry();
            entry.setTheaterId(theater.getTheaterId());
            entry.setTheaterName(theater.getTheaterName());
            entry.setTotalSeats(theater.getCapacity());

            // Get showings for this theater in the date range
            int seatsSold = countSeatsSoldForTheater(theater.getTheaterId(), fromDate, toDate);
            entry.setSeatsSold(seatsSold);

            // Calculate utilization percentage
            double utilization = 0;
            if (theater.getCapacity() > 0) {
                utilization = (double) seatsSold / theater.getCapacity() * 100;
            }
            entry.setUtilizationPercentage(Math.round(utilization * 10) / 10.0);

            // Get revenue for this theater
            BigDecimal revenue = calculateRevenueForTheater(theater.getTheaterId(), fromDate, toDate);
            entry.setRevenue(revenue);

            theaterStats.put(theater.getTheaterId(), entry);
        }

        // Sort entries by revenue in descending order
        List<TheaterUtilizationEntry> entries = new ArrayList<>(theaterStats.values());
        entries.sort((e1, e2) -> e2.getRevenue().compareTo(e1.getRevenue()));

        report.setEntries(entries);
        return report;
    }

    public DiscountUsageReport generateDiscountUsageReport(java.sql.Date fromDate, java.sql.Date toDate)
            throws SQLException {
        DiscountUsageReport report = new DiscountUsageReport();
        Map<Integer, DiscountUsageEntry> discountStats = new HashMap<>();

        // Get all discount types
        List<DiscountType> discounts = discountTypeDAO.getAllDiscountTypes();

        // Add "No Discount" entry
        DiscountUsageEntry noDiscountEntry = new DiscountUsageEntry();
        noDiscountEntry.setDiscountId(0);
        noDiscountEntry.setDiscountName("No Discount");
        discountStats.put(0, noDiscountEntry);

        for (DiscountType discount : discounts) {
            DiscountUsageEntry entry = new DiscountUsageEntry();
            entry.setDiscountId(discount.getDiscountId());
            entry.setDiscountName(discount.getDiscountName());
            discountStats.put(discount.getDiscountId(), entry);
        }

        // Query to get booking data with discount information
        String query = "SELECT b.discount_id, COUNT(b.booking_id) as booking_count, " +
                "SUM(b.total_amount) as discounted_revenue " +
                "FROM bookings b " +
                "WHERE b.booking_date BETWEEN ? AND ? " +
                "GROUP BY b.discount_id";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, fromDate);
            statement.setDate(2, toDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Integer discountId = resultSet.getInt("discount_id");
                    if (resultSet.wasNull()) {
                        discountId = 0; // No discount
                    }

                    int bookingCount = resultSet.getInt("booking_count");
                    BigDecimal discountedRevenue = resultSet.getBigDecimal("discounted_revenue");

                    DiscountUsageEntry entry = discountStats.get(discountId);
                    if (entry != null) {
                        entry.setTimesUsed(bookingCount);
                        entry.setRevenueAfterDiscount(discountedRevenue);

                        // Calculate original price before discount
                        BigDecimal originalRevenue = calculateOriginalRevenue(discountId, discountedRevenue);
                        entry.setRevenueBeforeDiscount(originalRevenue);

                        // Calculate savings
                        entry.setSavings(originalRevenue.subtract(discountedRevenue));
                    }
                }
            }
        }

        // Remove unused discounts and sort by times used
        List<DiscountUsageEntry> entries = new ArrayList<>();
        for (DiscountUsageEntry entry : discountStats.values()) {
            if (entry.getTimesUsed() > 0) {
                entries.add(entry);
            }
        }

        entries.sort((e1, e2) -> Integer.compare(e2.getTimesUsed(), e1.getTimesUsed()));

        report.setEntries(entries);
        return report;
    }

    private int countShowingsForMovie(int movieId, java.sql.Date fromDate, java.sql.Date toDate) throws SQLException {
        String query = "SELECT COUNT(*) FROM showings WHERE movie_id = ? AND show_date BETWEEN ? AND ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, movieId);
            statement.setDate(2, fromDate);
            statement.setDate(3, toDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }

    private double calculateAverageOccupancyForMovie(int movieId, java.sql.Date fromDate, java.sql.Date toDate)
            throws SQLException {
        String query = "SELECT s.showing_id, COUNT(t.ticket_id) as tickets_sold, th.capacity " +
                "FROM showings s " +
                "JOIN theaters th ON s.theater_id = th.theater_id " +
                "LEFT JOIN bookings b ON s.showing_id = b.showing_id " +
                "LEFT JOIN tickets t ON b.booking_id = t.booking_id " +
                "WHERE s.movie_id = ? AND s.show_date BETWEEN ? AND ? " +
                "GROUP BY s.showing_id";

        int totalTickets = 0;
        int totalCapacity = 0;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, movieId);
            statement.setDate(2, fromDate);
            statement.setDate(3, toDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int ticketsSold = resultSet.getInt("tickets_sold");
                    int capacity = resultSet.getInt("capacity");

                    totalTickets += ticketsSold;
                    totalCapacity += capacity;
                }
            }
        }

        if (totalCapacity > 0) {
            return Math.round((double) totalTickets / totalCapacity * 1000) / 10.0;
        }

        return 0;
    }

    private int countSeatsSoldForTheater(int theaterId, java.sql.Date fromDate, java.sql.Date toDate)
            throws SQLException {
        String query = "SELECT COUNT(t.ticket_id) " +
                "FROM showings s " +
                "JOIN bookings b ON s.showing_id = b.showing_id " +
                "JOIN tickets t ON b.booking_id = t.booking_id " +
                "WHERE s.theater_id = ? AND s.show_date BETWEEN ? AND ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, theaterId);
            statement.setDate(2, fromDate);
            statement.setDate(3, toDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }

    private BigDecimal calculateRevenueForTheater(int theaterId, java.sql.Date fromDate, java.sql.Date toDate)
            throws SQLException {
        String query = "SELECT SUM(b.total_amount) " +
                "FROM showings s " +
                "JOIN bookings b ON s.showing_id = b.showing_id " +
                "WHERE s.theater_id = ? AND s.show_date BETWEEN ? AND ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, theaterId);
            statement.setDate(2, fromDate);
            statement.setDate(3, toDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal revenue = resultSet.getBigDecimal(1);
                    return revenue != null ? revenue : BigDecimal.ZERO;
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calculateOriginalRevenue(int discountId, BigDecimal discountedRevenue) throws SQLException {
        if (discountId == 0) {
            return discountedRevenue; // No discount applied
        }

        DiscountType discount = discountTypeDAO.getDiscountTypeById(discountId);
        if (discount != null) {
            BigDecimal discountRate = BigDecimal.ONE.subtract(
                    discount.getDiscountPercentage().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));

            if (discountRate.compareTo(BigDecimal.ZERO) > 0) {
                return discountedRevenue.divide(discountRate, 2, RoundingMode.HALF_UP);
            }
        }

        return discountedRevenue;
    }
}
