package ui;

import model.Movie;
import model.Showing;
import model.Theater;
import service.MovieService;
import service.ShowingService;
import service.TheaterService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShowingFormDialog extends JDialog {
    private Showing showing;
    private ShowingService showingService;
    private MovieService movieService;
    private TheaterService theaterService;
    private boolean isNewShowing;

    private JComboBox<Movie> movieComboBox;
    private JComboBox<Theater> theaterComboBox;
    private JTextField dateField;
    private JTextField timeField;
    private JTextField basePriceField;

    public ShowingFormDialog(JFrame parent, ShowingService showingService, Showing showing) {
        super(parent, showing == null ? "Add New Showing" : "Edit Showing", true);
        this.showingService = showingService;
        this.showing = showing;
        this.isNewShowing = (showing == null);
        this.movieService = new MovieService();
        this.theaterService = new TheaterService();

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Movie
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Movie:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        movieComboBox = new JComboBox<>();
        loadMovies();
        formPanel.add(movieComboBox, gbc);

        // Theater
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Theater:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        theaterComboBox = new JComboBox<>();
        loadTheaters();
        formPanel.add(theaterComboBox, gbc);

        // Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        dateField = new JTextField(10);
        formPanel.add(dateField, gbc);

        // Time
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Time (HH:mm:ss):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        timeField = new JTextField(10);
        formPanel.add(timeField, gbc);

        // Base Price
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Base Price ($):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        basePriceField = new JTextField(10);
        formPanel.add(basePriceField, gbc);

        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveShowing());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Fill form with showing data if editing
        if (!isNewShowing) {
            populateForm();
        } else {
            // Set defaults for new showing
            dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            timeField.setText("18:00:00");
            basePriceField.setText("10.00");
        }

        add(mainPanel);
    }

    private void loadMovies() {
        try {
            List<Movie> movies = movieService.getAllMovies();
            for (Movie movie : movies) {
                movieComboBox.addItem(movie);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading movies: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTheaters() {
        try {
            List<Theater> theaters = theaterService.getAllTheaters();
            for (Theater theater : theaters) {
                theaterComboBox.addItem(theater);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading theaters: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void populateForm() {
        try {
            // Set selected movie
            Movie movie = showingService.getMovieForShowing(showing.getShowingId());
            if (movie != null) {
                for (int i = 0; i < movieComboBox.getItemCount(); i++) {
                    Movie item = movieComboBox.getItemAt(i);
                    if (item.getMovieId() == movie.getMovieId()) {
                        movieComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            // Set selected theater
            Theater theater = showingService.getTheaterForShowing(showing.getShowingId());
            if (theater != null) {
                for (int i = 0; i < theaterComboBox.getItemCount(); i++) {
                    Theater item = theaterComboBox.getItemAt(i);
                    if (item.getTheaterId() == theater.getTheaterId()) {
                        theaterComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            // Set date and time
            if (showing.getShowDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateField.setText(dateFormat.format(showing.getShowDate()));
            }

            if (showing.getShowTime() != null) {
                timeField.setText(showing.getShowTime().toString());
            }

            // Set base price
            if (showing.getBasePrice() != null) {
                basePriceField.setText(showing.getBasePrice().toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading showing details: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveShowing() {
        Movie selectedMovie = (Movie) movieComboBox.getSelectedItem();
        Theater selectedTheater = (Theater) theaterComboBox.getSelectedItem();
        String dateStr = dateField.getText().trim();
        String timeStr = timeField.getText().trim();
        String basePriceStr = basePriceField.getText().trim();

        // Validation
        if (selectedMovie == null || selectedTheater == null || dateStr.isEmpty() || timeStr.isEmpty()
                || basePriceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date showDate;
        Time showTime;
        BigDecimal basePrice;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(dateStr);
            showDate = new Date(parsedDate.getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use yyyy-MM-dd",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            java.util.Date parsedTime = timeFormat.parse(timeStr);
            showTime = new Time(parsedTime.getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid time format. Please use HH:mm:ss",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            basePrice = new BigDecimal(basePriceStr);
            if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Price must be positive");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid price. Please enter a positive number",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (isNewShowing) {
                // Create new showing
                showingService.addShowing(
                        selectedMovie.getMovieId(),
                        selectedTheater.getTheaterId(),
                        showDate,
                        showTime,
                        basePrice);

                JOptionPane.showMessageDialog(this,
                        "Showing added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing showing
                showingService.updateShowing(
                        showing.getShowingId(),
                        selectedMovie.getMovieId(),
                        selectedTheater.getTheaterId(),
                        showDate,
                        showTime,
                        basePrice);

                JOptionPane.showMessageDialog(this,
                        "Showing updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving showing: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
