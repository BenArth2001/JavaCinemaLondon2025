package ui;

import model.Movie;
import service.MovieService;
import util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class MovieBrowsingScreen extends JFrame {
    private MainApplication mainApp;
    private MovieService movieService;
    private JPanel moviesPanel;

    public MovieBrowsingScreen(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.movieService = new MovieService();

        // Set up the frame
        setTitle("Cinema Booking System - Movies");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 50, 80));
        headerPanel.setPreferredSize(new Dimension(800, 60));

        JLabel titleLabel = new JLabel("Now Showing");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            if (mainApp.getUserService().isLoggedIn()) {
                // If user is logged in, go back to dashboard
                if (mainApp.getUserService().isAdmin()) {
                    mainApp.showAdminDashboard();
                } else if (mainApp.getUserService().isEmployee()) {
                    mainApp.showEmployeeDashboard();
                } else {
                    mainApp.showCustomerDashboard();
                }
            } else {
                // If not logged in, go to login screen
                mainApp.showLoginScreen();
            }
        });
        buttonsPanel.add(backButton);

        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create movies panel
        moviesPanel = new JPanel();
        moviesPanel.setLayout(new BoxLayout(moviesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(moviesPanel);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Load movies
        loadMovies();

        add(mainPanel);
    }

    private void loadMovies() {
        moviesPanel.removeAll();

        try {
            List<Movie> movies = movieService.getActiveMovies();

            if (movies.isEmpty()) {
                JLabel noMoviesLabel = new JLabel("No movies available.");
                noMoviesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                moviesPanel.add(noMoviesLabel);
            } else {
                for (Movie movie : movies) {
                    JPanel moviePanel = createMoviePanel(movie);
                    moviesPanel.add(moviePanel);
                    moviesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading movies: " + e.getMessage());
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            moviesPanel.add(errorLabel);
            e.printStackTrace();
        }

        moviesPanel.revalidate();
        moviesPanel.repaint();
    }

    private JPanel createMoviePanel(Movie movie) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        panel.setMaximumSize(new Dimension(780, 180));

        // Add poster image to the left
        JLabel posterLabel = new JLabel(ImageUtils.getScaledImage(movie.getPosterUrl()));
        posterLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        panel.add(posterLabel, BorderLayout.WEST);

        // Panel for title and info
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.setOpaque(false);

        // Add movie details
        if (movie.getDescription() != null && !movie.getDescription().isEmpty()) {
            JTextArea descArea = new JTextArea(movie.getDescription());
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setOpaque(false);
            descArea.setRows(3);
            infoPanel.add(descArea);
        }

        JLabel detailsLabel = new JLabel("Duration: " + movie.getDurationMinutes() + " min" +
                (movie.getRating() != null ? " | Rating: " + movie.getRating() : ""));
        infoPanel.add(detailsLabel);

        contentPanel.add(infoPanel, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton showTimesButton = new JButton("Book Tickets");
        showTimesButton.addActionListener(e -> {
            if (!mainApp.getUserService().isLoggedIn()) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "You need to be logged in to book tickets. Would you like to log in now?",
                        "Login Required",
                        JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    mainApp.showLoginScreen();
                }
            } else {
                // Open the showing selection screen for this movie
                ShowingSelectionScreen screen = new ShowingSelectionScreen(mainApp, movie.getMovieId());
                screen.setVisible(true);
            }
        });
        buttonPanel.add(showTimesButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
}
