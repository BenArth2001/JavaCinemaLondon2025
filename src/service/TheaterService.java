package service;

import dao.TheaterDAO;
import dao.SeatDAO;
import model.Theater;
import model.Seat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheaterService {
    private TheaterDAO theaterDAO;
    private SeatDAO seatDAO;

    public TheaterService() {
        this.theaterDAO = new TheaterDAO();
        this.seatDAO = new SeatDAO();
    }

    public List<Theater> getAllTheaters() throws SQLException {
        return theaterDAO.getAllTheaters();
    }

    public Theater getTheaterById(int theaterId) throws SQLException {
        return theaterDAO.getTheaterById(theaterId);
    }

    public void addTheater(String theaterName, String location, int capacity) throws SQLException {
        Theater theater = new Theater(0, theaterName, location, capacity);
        theaterDAO.addTheater(theater);
    }

    public void updateTheater(int theaterId, String theaterName, String location, int capacity) throws SQLException {
        Theater theater = new Theater(theaterId, theaterName, location, capacity);
        theaterDAO.updateTheater(theater);
    }

    public void deleteTheater(int theaterId) throws SQLException {
        theaterDAO.deleteTheater(theaterId);
    }

    public List<Seat> getAllSeatsForTheater(int theaterId) throws SQLException {
        return seatDAO.getSeatsByTheater(theaterId);
    }

    public List<Seat> getAvailableSeatsForShowing(int showingId) throws SQLException {
        return seatDAO.getAvailableSeatsForShowing(showingId);
    }

    public Map<Character, List<Seat>> getSeatsGroupedByRow(int theaterId) throws SQLException {
        List<Seat> seats = seatDAO.getSeatsByTheater(theaterId);
        Map<Character, List<Seat>> seatsByRow = new HashMap<>();

        for (Seat seat : seats) {
            char row = seat.getSeatRow();
            if (!seatsByRow.containsKey(row)) {
                seatsByRow.put(row, new ArrayList<>());
            }
            seatsByRow.get(row).add(seat);
        }

        return seatsByRow;
    }

    public void addSeat(int theaterId, char seatRow, int seatNumber, String seatType) throws SQLException {
        Seat seat = new Seat(0, theaterId, seatRow, seatNumber, seatType);
        seatDAO.addSeat(seat);
    }

    public void updateSeat(int seatId, int theaterId, char seatRow, int seatNumber, String seatType)
            throws SQLException {
        Seat seat = new Seat(seatId, theaterId, seatRow, seatNumber, seatType);
        seatDAO.updateSeat(seat);
    }

    public Seat getSeatById(int seatId) throws SQLException {
        return seatDAO.getSeatById(seatId);
    }

    // Method to automatically generate seats for a theater
    public void generateTheaterSeats(int theaterId, int rows, int seatsPerRow) throws SQLException {
        Theater theater = theaterDAO.getTheaterById(theaterId);

        if (theater != null) {
            for (int r = 0; r < rows; r++) {
                char rowChar = (char) ('A' + r);
                String seatType = "standard";

                // Set premium seats in the middle rows
                if (r >= rows / 3 && r < 2 * rows / 3) {
                    seatType = "premium";
                }
                // Set VIP seats in the back rows
                else if (r >= 2 * rows / 3) {
                    seatType = "vip";
                }

                for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                    Seat seat = new Seat(0, theaterId, rowChar, seatNum, seatType);
                    seatDAO.addSeat(seat);
                }
            }

            // Update theater capacity
            theater.setCapacity(rows * seatsPerRow);
            theaterDAO.updateTheater(theater);
        }
    }

    /**
     * Check if a theater has any seats configured.
     * 
     * @param theaterId The ID of the theater to check
     * @return true if the theater has at least one seat, false otherwise
     * @throws SQLException if there's a database error
     */
    public boolean theaterHasSeats(int theaterId) throws SQLException {
        return !getAllSeatsForTheater(theaterId).isEmpty();
    }

    /**
     * Ensure theater has seats. If it doesn't, generate a default seating
     * arrangement.
     * 
     * @param theaterId The ID of the theater to check
     * @throws SQLException if there's a database error
     */
    public void ensureTheaterHasSeats(int theaterId) throws SQLException {
        if (!theaterHasSeats(theaterId)) {
            Theater theater = getTheaterById(theaterId);
            if (theater != null) {
                // Default configuration: 8 rows, 10 seats per row
                generateTheaterSeats(theaterId, 8, 10);
            }
        }
    }
}
