package ui;

import model.Booking;
import model.Movie;
import model.User;
import service.BookingService;
import service.MovieService;
import service.UserService;
import util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class CustomerDashboard extends JFrame {
    private MainApplication mainApp;
    private User currentUser;
    private MovieService movieService;
    private BookingService bookingService;

    private JPanel mainPanel;
    private JPanel moviesPanel;
    private JPanel bookingsPanel;

    public CustomerDashboard(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.currentUser = mainApp.getUserService().getCurrentUser();
        this.movieService = new MovieService();
        this.bookingService = new BookingService();

        // Set up the frame
        setTitle("Cinema Booking System - Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Create main panel
        mainPanel = new JPanel(new BorderLayout());

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tab panel
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create movies panel
        moviesPanel = new JPanel(new BorderLayout());
        loadMoviesPanel();
        tabbedPane.addTab("Movies", moviesPanel);

        // Create bookings panel
        bookingsPanel = new JPanel(new BorderLayout());
        loadBookingsPanel();
        tabbedPane.addTab("My Bookings", bookingsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 50, 80));
        headerPanel.setPreferredSize(new Dimension(900, 60));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFirstName() + "!");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.getUserService().logout();
                mainApp.showLoginScreen();
            }
        });
        buttonsPanel.add(logoutButton);

        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void loadMoviesPanel() {
        moviesPanel.removeAll();

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Available Movies");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel);

        JPanel moviesListPanel = new JPanel();
        moviesListPanel.setLayout(new BoxLayout(moviesListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(moviesListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        try {
            List<Movie> movies = movieService.getActiveMovies();

            if (movies.isEmpty()) {
                JLabel noMoviesLabel = new JLabel("No movies available at the moment.");
                noMoviesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                moviesListPanel.add(noMoviesLabel);
            } else {
                for (Movie movie : movies) {
                    JPanel moviePanel = createMoviePanel(movie);
                    moviesListPanel.add(moviePanel);
                    // Add some space between movie panels
                    moviesListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading movies: " + e.getMessage());
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            moviesListPanel.add(errorLabel);
            e.printStackTrace();
        }

        moviesPanel.add(headerPanel, BorderLayout.NORTH);
        moviesPanel.add(scrollPane, BorderLayout.CENTER);

        moviesPanel.revalidate();
        moviesPanel.repaint();
    }

    private JPanel createMoviePanel(Movie movie) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setMaximumSize(new Dimension(800, 150));

        // Add poster image to the left
        JLabel posterLabel = new JLabel(ImageUtils.getScaledImage(movie.getPosterUrl()));
        posterLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        panel.add(posterLabel, BorderLayout.WEST);

        // Panel for title and other information
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(titleLabel);

        try {
            String genreName = movieService.getMovieGenreName(movie.getGenreId());
            JLabel genreLabel = new JLabel("Genre: " + genreName);
            infoPanel.add(genreLabel);
        } catch (SQLException e) {
            // Ignore genre if error
        }

        JLabel durationLabel = new JLabel("Duration: " + movie.getDurationMinutes() + " minutes");
        infoPanel.add(durationLabel);

        if (movie.getRating() != null) {
            JLabel ratingLabel = new JLabel("Rating: " + movie.getRating());
            infoPanel.add(ratingLabel);
        }

        if (movie.getDescription() != null) {
            JTextArea descArea = new JTextArea(movie.getDescription());
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setBackground(panel.getBackground());
            descArea.setRows(2);
            infoPanel.add(descArea);
        }

        panel.add(infoPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("Book Tickets");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open booking screen for this movie
                ShowingSelectionScreen screen = new ShowingSelectionScreen(mainApp, movie.getMovieId());
                screen.setVisible(true);
            }
        });
        buttonPanel.add(bookButton);

        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private void loadBookingsPanel() {
        bookingsPanel.removeAll();

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("My Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel);

        JPanel bookingsListPanel = new JPanel();
        bookingsListPanel.setLayout(new BoxLayout(bookingsListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(bookingsListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        try {
            List<Booking> bookings = bookingService.getBookingsByUser(currentUser.getUserId());

            if (bookings.isEmpty()) {
                JLabel noBookingsLabel = new JLabel("You don't have any bookings yet.");
                noBookingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                bookingsListPanel.add(noBookingsLabel);
            } else {
                for (Booking booking : bookings) {
                    // In a real implementation, you would create a more detailed booking panel
                    JPanel bookingPanel = new JPanel();
                    bookingPanel.setLayout(new BorderLayout());
                    bookingPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200)),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                    bookingPanel.setMaximumSize(new Dimension(800, 100));

                    JLabel bookingLabel = new JLabel("Booking #" + booking.getBookingId() +
                            " - Date: " + booking.getBookingDate() +
                            " - Amount: $" + booking.getTotalAmount());
                    bookingPanel.add(bookingLabel, BorderLayout.CENTER);

                    bookingsListPanel.add(bookingPanel);
                    bookingsListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading bookings: " + e.getMessage());
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            bookingsListPanel.add(errorLabel);
            e.printStackTrace();
        }

        bookingsPanel.add(headerPanel, BorderLayout.NORTH);
        bookingsPanel.add(scrollPane, BorderLayout.CENTER);

        bookingsPanel.revalidate();
        bookingsPanel.repaint();
    }
}
