package ui;

import model.Genre;
import model.Movie;
import service.MovieService;
import util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MovieFormDialog extends JDialog {
    private JFrame parentFrame;
    private MovieService movieService;
    private Movie existingMovie;

    // Form fields
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField durationField;
    private JTextField releaseDateField;
    private JComboBox<Genre> genreComboBox;
    private JTextField ratingField;
    private JTextField posterUrlField;
    private JLabel posterPreviewLabel;
    private JCheckBox isActiveCheckbox;
    private JButton selectImageButton;
    private JButton saveButton;
    private JButton cancelButton;

    // Path to the chosen poster image
    private File selectedImageFile = null;

    public MovieFormDialog(JFrame parent, MovieService movieService, Movie movie) {
        super(parent, movie == null ? "Add New Movie" : "Edit Movie", true);
        this.parentFrame = parent;
        this.movieService = movieService;
        this.existingMovie = movie;

        // Set up the dialog
        setSize(600, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Initialize components
        initComponents();

        // Fill in fields if editing existing movie
        if (existingMovie != null) {
            populateFields();
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        titleField = new JTextField(30);
        formPanel.add(titleField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        // Duration
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Duration (min):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        durationField = new JTextField(10);
        formPanel.add(durationField, gbc);

        // Release Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Release Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        releaseDateField = new JTextField(10);
        formPanel.add(releaseDateField, gbc);

        // Genre
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Genre:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        genreComboBox = new JComboBox<>();
        loadGenres();
        formPanel.add(genreComboBox, gbc);

        // Rating
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Rating:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        ratingField = new JTextField(10);
        formPanel.add(ratingField, gbc);

        // Poster URL
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Poster Image:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        posterUrlField = new JTextField(20);
        posterUrlField.setEditable(false);
        formPanel.add(posterUrlField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 6;
        selectImageButton = new JButton("Browse...");
        selectImageButton.addActionListener(e -> selectPosterImage());
        formPanel.add(selectImageButton, gbc);

        // Poster Preview
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Preview:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        posterPreviewLabel = new JLabel(ImageUtils.getPlaceholderImage());
        posterPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(posterPreviewLabel, gbc);

        // Is Active
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Active:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        isActiveCheckbox = new JCheckBox();
        isActiveCheckbox.setSelected(true);
        formPanel.add(isActiveCheckbox, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveMovie());

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadGenres() {
        try {
            List<Genre> genres = movieService.getAllGenres();
            for (Genre genre : genres) {
                genreComboBox.addItem(genre);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading genres: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void populateFields() {
        if (existingMovie != null) {
            titleField.setText(existingMovie.getTitle());
            descriptionArea.setText(existingMovie.getDescription());
            durationField.setText(String.valueOf(existingMovie.getDurationMinutes()));

            if (existingMovie.getReleaseDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                releaseDateField.setText(dateFormat.format(existingMovie.getReleaseDate()));
            }

            for (int i = 0; i < genreComboBox.getItemCount(); i++) {
                Genre genre = genreComboBox.getItemAt(i);
                if (genre.getGenreId() == existingMovie.getGenreId()) {
                    genreComboBox.setSelectedIndex(i);
                    break;
                }
            }

            ratingField.setText(existingMovie.getRating());
            posterUrlField.setText(existingMovie.getPosterUrl());
            isActiveCheckbox.setSelected(existingMovie.isActive());

            // Load and display the existing poster image
            if (existingMovie.getPosterUrl() != null && !existingMovie.getPosterUrl().isEmpty()) {
                posterPreviewLabel.setIcon(ImageUtils.getScaledImage(existingMovie.getPosterUrl()));
            }
        }
    }

    private void selectPosterImage() {
        // Create file chooser
        JFileChooser fileChooser = new JFileChooser();

        // Set the initial directory to the images folder
        File imagesDir = new File("out/production/CinemaJavaPro/images");
        if (imagesDir.exists() && imagesDir.isDirectory()) {
            fileChooser.setCurrentDirectory(imagesDir);
        } else {
            // Try to create the directory if it doesn't exist
            imagesDir.mkdirs();
            if (imagesDir.exists()) {
                fileChooser.setCurrentDirectory(imagesDir);
            }
        }

        fileChooser.setDialogTitle("Select Poster Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                        name.endsWith(".png") || name.endsWith(".gif");
            }

            @Override
            public String getDescription() {
                return "Image files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            posterUrlField.setText(selectedImageFile.getName());

            // Show preview of selected image
            ImageIcon icon = new ImageIcon(selectedImageFile.getPath());
            posterPreviewLabel.setIcon(ImageUtils.getScaledImage(icon));
        }
    }

    private void saveMovie() {
        // Validate input
        if (titleField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title is required",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Duration must be a number",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date releaseDate = null;
        if (!releaseDateField.getText().isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                releaseDate = dateFormat.parse(releaseDateField.getText());
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Use yyyy-MM-dd",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Genre selectedGenre = (Genre) genreComboBox.getSelectedItem();
        if (selectedGenre == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a genre",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Handle the poster image
        String posterUrl = null;
        if (selectedImageFile != null) {
            try {
                // Ensure the image directory exists
                ImageUtils.ensureImageDirectoryExists();

                // Generate unique filename for the image
                String extension = selectedImageFile.getName().substring(selectedImageFile.getName().lastIndexOf('.'));
                String newFileName = UUID.randomUUID().toString() + extension;

                // Copy the file to the images directory
                Path sourcePath = selectedImageFile.toPath();
                Path targetPath = Paths.get("out/production/CinemaJavaPro/images", newFileName);
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                posterUrl = newFileName;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error saving image: " + e.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return;
            }
        } else if (existingMovie != null) {
            // Keep existing poster URL if no new image is selected
            posterUrl = existingMovie.getPosterUrl();
        }

        try {
            if (existingMovie == null) {
                // Add new movie
                movieService.addMovie(
                        titleField.getText(),
                        descriptionArea.getText(),
                        duration,
                        releaseDate,
                        selectedGenre.getGenreId(),
                        ratingField.getText(),
                        posterUrl,
                        isActiveCheckbox.isSelected());

                JOptionPane.showMessageDialog(this,
                        "Movie added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing movie
                movieService.updateMovie(
                        existingMovie.getMovieId(),
                        titleField.getText(),
                        descriptionArea.getText(),
                        duration,
                        releaseDate,
                        selectedGenre.getGenreId(),
                        ratingField.getText(),
                        posterUrl,
                        isActiveCheckbox.isSelected());

                JOptionPane.showMessageDialog(this,
                        "Movie updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
