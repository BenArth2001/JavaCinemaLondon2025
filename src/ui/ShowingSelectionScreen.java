package ui;

import model.Movie;
import model.Showing;
import model.Theater;
import service.MovieService;
import service.ShowingService;
import service.TheaterService;
import util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShowingSelectionScreen extends JFrame {
    private MainApplication mainApp;
    private int movieId;
    private Movie movie;
    private ShowingService showingService;
    private TheaterService theaterService;
    private MovieService movieService;

    public ShowingSelectionScreen(MainApplication mainApp, int movieId) {
        this.mainApp = mainApp;
        this.movieId = movieId;
        this.showingService = new ShowingService();
        this.theaterService = new TheaterService();
        this.movieService = new MovieService();

        // Set up the frame
        setTitle("Select Showing");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            movie = movieService.getMovieById(movieId);
            if (movie == null) {
                JOptionPane.showMessageDialog(this, "Movie not found", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            initComponents();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading movie: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose();
        }
    }

    private void initComponents() throws SQLException {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Movie info panel
        JPanel movieInfoPanel = new JPanel(new BorderLayout(15, 0));
        movieInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Movie Information"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Add movie poster to the left
        JLabel posterLabel = new JLabel(ImageUtils.getScaledImage(movie.getPosterUrl()));
        movieInfoPanel.add(posterLabel, BorderLayout.WEST);

        // Content panel for movie details
        JPanel detailsContainer = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        detailsContainer.add(titleLabel, BorderLayout.NORTH);

        JTextArea descriptionArea = new JTextArea(movie.getDescription());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(movieInfoPanel.getBackground());
        detailsContainer.add(descriptionArea, BorderLayout.CENTER);

        String genreName = "Unknown";
        try {
            genreName = movieService.getMovieGenreName(movie.getGenreId());
        } catch (SQLException e) {
            // Ignore
        }

        JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        detailsPanel.add(new JLabel("Duration:"));
        detailsPanel.add(new JLabel(movie.getDurationMinutes() + " minutes"));
        detailsPanel.add(new JLabel("Genre:"));
        detailsPanel.add(new JLabel(genreName));
        detailsPanel.add(new JLabel("Rating:"));
        detailsPanel.add(new JLabel(movie.getRating() != null ? movie.getRating() : "Not rated"));
        detailsContainer.add(detailsPanel, BorderLayout.SOUTH);

        movieInfoPanel.add(detailsContainer, BorderLayout.CENTER);
        mainPanel.add(movieInfoPanel, BorderLayout.NORTH);

        // Showings panel
        JPanel showingsPanel = new JPanel();
        showingsPanel.setLayout(new BoxLayout(showingsPanel, BoxLayout.Y_AXIS));
        showingsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Available Showings"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        List<Showing> showings = showingService.getShowingsByMovie(movieId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        if (showings.isEmpty()) {
            showingsPanel.add(new JLabel("No showings available for this movie."));
        } else {
            for (Showing showing : showings) {
                Theater theater = theaterService.getTheaterById(showing.getTheaterId());

                JPanel showingPanel = new JPanel(new BorderLayout());
                showingPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));

                String dateStr = dateFormat.format(showing.getShowDate());
                String timeStr = timeFormat.format(showing.getShowTime());
                String theaterStr = theater != null ? theater.getTheaterName() : "Unknown Theater";

                JLabel showingLabel = new JLabel(dateStr + " at " + timeStr + " - " + theaterStr);
                showingPanel.add(showingLabel, BorderLayout.WEST);

                JLabel priceLabel = new JLabel("Price: $" + showing.getBasePrice());
                showingPanel.add(priceLabel, BorderLayout.CENTER);

                JButton selectButton = new JButton("Select");
                int showingId = showing.getShowingId();
                selectButton.addActionListener(e -> selectShowing(showingId));
                showingPanel.add(selectButton, BorderLayout.EAST);

                showingsPanel.add(showingPanel);
                showingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(showingsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back to Movies");
        backButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void selectShowing(int showingId) {
        SeatSelectionScreen seatSelection = new SeatSelectionScreen(mainApp, showingId);
        seatSelection.setVisible(true);
        dispose(); // Close this screen
    }
}
