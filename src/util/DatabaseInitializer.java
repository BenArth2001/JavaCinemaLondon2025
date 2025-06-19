package util;

import model.Theater;
import service.TheaterService;

import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;

public class DatabaseInitializer {

    private static final int DEFAULT_ROWS = 8;
    private static final int DEFAULT_SEATS_PER_ROW = 10;

    /**
     * Initialize the database with necessary data.
     * Currently ensures that all theaters have seats.
     */
    public static void initialize() {
        try {
            // Ensure theaters have seats
            ensureTheatersHaveSeats();

            // Ensure images directory exists
            ImageUtils.ensureImageDirectoryExists();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error initializing database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Ensure all theaters have seats. If a theater has no seats,
     * generate default seating arrangement.
     */
    private static void ensureTheatersHaveSeats() throws SQLException {
        TheaterService theaterService = new TheaterService();
        List<Theater> theaters = theaterService.getAllTheaters();

        for (Theater theater : theaters) {
            if (theaterService.getAllSeatsForTheater(theater.getTheaterId()).isEmpty()) {
                System.out.println("Generating seats for theater: " + theater.getTheaterName());
                theaterService.generateTheaterSeats(theater.getTheaterId(), DEFAULT_ROWS, DEFAULT_SEATS_PER_ROW);
            }
        }
    }
}
