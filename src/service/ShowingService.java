package service;

import dao.MovieDAO;
import dao.ShowingDAO;
import dao.TheaterDAO;
import model.Movie;
import model.Showing;
import model.Theater;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class ShowingService {
    private ShowingDAO showingDAO;
    private MovieDAO movieDAO;
    private TheaterDAO theaterDAO;

    public ShowingService() {
        this.showingDAO = new ShowingDAO();
        this.movieDAO = new MovieDAO();
        this.theaterDAO = new TheaterDAO();
    }

    public List<Showing> getAllShowings() throws SQLException {
        return showingDAO.getAllShowings();
    }

    public Showing getShowingById(int showingId) throws SQLException {
        return showingDAO.getShowingById(showingId);
    }

    public List<Showing> getShowingsByMovie(int movieId) throws SQLException {
        return showingDAO.getShowingsByMovie(movieId);
    }

    public List<Showing> getShowingsByTheater(int theaterId) throws SQLException {
        return showingDAO.getShowingsByTheater(theaterId);
    }

    public List<Showing> getShowingsByDate(Date date) throws SQLException {
        return showingDAO.getShowingsByDate(date);
    }

    public void addShowing(int movieId, int theaterId, Date showDate, Time showTime, BigDecimal basePrice)
            throws SQLException {
        Showing showing = new Showing(0, movieId, theaterId, showDate, showTime, basePrice);
        showingDAO.addShowing(showing);
    }

    public void updateShowing(int showingId, int movieId, int theaterId, Date showDate, Time showTime,
            BigDecimal basePrice) throws SQLException {
        Showing showing = new Showing(showingId, movieId, theaterId, showDate, showTime, basePrice);
        showingDAO.updateShowing(showing);
    }

    public void deleteShowing(int showingId) throws SQLException {
        showingDAO.deleteShowing(showingId);
    }

    public Movie getMovieForShowing(int showingId) throws SQLException {
        Showing showing = showingDAO.getShowingById(showingId);
        if (showing != null) {
            return movieDAO.getMovieById(showing.getMovieId());
        }
        return null;
    }

    public Theater getTheaterForShowing(int showingId) throws SQLException {
        Showing showing = showingDAO.getShowingById(showingId);
        if (showing != null) {
            return theaterDAO.getTheaterById(showing.getTheaterId());
        }
        return null;
    }

    public List<Object[]> getShowingsWithDetails() throws SQLException {
        List<Object[]> showingsWithDetails = new ArrayList<>();
        List<Showing> showings = showingDAO.getAllShowings();

        for (Showing showing : showings) {
            Movie movie = movieDAO.getMovieById(showing.getMovieId());
            Theater theater = theaterDAO.getTheaterById(showing.getTheaterId());

            if (movie != null && theater != null) {
                Object[] details = { showing, movie, theater };
                showingsWithDetails.add(details);
            }
        }

        return showingsWithDetails;
    }

    public BigDecimal getShowingPrice(int showingId, String seatType) throws SQLException {
        Showing showing = showingDAO.getShowingById(showingId);
        BigDecimal basePrice = showing.getBasePrice();

        // Apply pricing based on seat type
        if ("premium".equalsIgnoreCase(seatType)) {
            return basePrice.multiply(new BigDecimal("1.25")); // 25% premium
        } else if ("vip".equalsIgnoreCase(seatType)) {
            return basePrice.multiply(new BigDecimal("1.5")); // 50% premium
        }

        return basePrice; // Standard seat
    }

    public List<Showing> getShowingsByMovieAndTheater(int movieId, int theaterId) throws SQLException {
        List<Showing> showings = new ArrayList<>();
        String query = "SELECT * FROM showings WHERE movie_id = ? AND theater_id = ? ORDER BY show_date, show_time";

        try (java.sql.Connection connection = database.DatabaseConnection.getConnection();
                java.sql.PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, movieId);
            statement.setInt(2, theaterId);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    showings.add(extractShowingFromResultSet(resultSet));
                }
            }
        }

        return showings;
    }

    public List<Showing> getShowingsByMovieAndDate(int movieId, Date date) throws SQLException {
        List<Showing> showings = new ArrayList<>();
        String query = "SELECT * FROM showings WHERE movie_id = ? AND show_date = ? ORDER BY show_time";

        try (java.sql.Connection connection = database.DatabaseConnection.getConnection();
                java.sql.PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, movieId);
            statement.setDate(2, date);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    showings.add(extractShowingFromResultSet(resultSet));
                }
            }
        }

        return showings;
    }

    public List<Showing> getShowingsByTheaterAndDate(int theaterId, Date date) throws SQLException {
        List<Showing> showings = new ArrayList<>();
        String query = "SELECT * FROM showings WHERE theater_id = ? AND show_date = ? ORDER BY show_time";

        try (java.sql.Connection connection = database.DatabaseConnection.getConnection();
                java.sql.PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, theaterId);
            statement.setDate(2, date);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    showings.add(extractShowingFromResultSet(resultSet));
                }
            }
        }

        return showings;
    }

    public List<Showing> getShowingsByAll(int movieId, int theaterId, Date date) throws SQLException {
        List<Showing> showings = new ArrayList<>();
        String query = "SELECT * FROM showings WHERE movie_id = ? AND theater_id = ? AND show_date = ? ORDER BY show_time";

        try (java.sql.Connection connection = database.DatabaseConnection.getConnection();
                java.sql.PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, movieId);
            statement.setInt(2, theaterId);
            statement.setDate(3, date);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    showings.add(extractShowingFromResultSet(resultSet));
                }
            }
        }

        return showings;
    }

    private Showing extractShowingFromResultSet(java.sql.ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int movieId = resultSet.getInt("movie_id");
        int theaterId = resultSet.getInt("theater_id");
        Date showDate = resultSet.getDate("show_date");
        Time showTime = resultSet.getTime("show_time");
        BigDecimal basePrice = resultSet.getBigDecimal("base_price");

        return new Showing(id, movieId, theaterId, showDate, showTime, basePrice);
    }
}
