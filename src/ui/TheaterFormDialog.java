package ui;

import model.Theater;
import service.TheaterService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List; // Add this import

public class TheaterFormDialog extends JDialog {
    private Theater theater;
    private TheaterService theaterService;
    private boolean isNewTheater;

    private JTextField nameField;
    private JTextField locationField;
    private JTextField capacityField;
    private JCheckBox generateSeatsCheckBox;
    private JSpinner rowsSpinner;
    private JSpinner seatsPerRowSpinner;

    public TheaterFormDialog(JFrame parent, TheaterService theaterService, Theater theater) {
        super(parent, theater == null ? "Add New Theater" : "Edit Theater", true);
        this.theaterService = theaterService;
        this.theater = theater;
        this.isNewTheater = (theater == null);

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

        // Theater Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Theater Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Location
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        locationField = new JTextField(20);
        formPanel.add(locationField, gbc);

        // Capacity
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Capacity:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        capacityField = new JTextField(10);
        formPanel.add(capacityField, gbc);

        // Generate Seats
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        generateSeatsCheckBox = new JCheckBox("Generate seating arrangement automatically");
        generateSeatsCheckBox.setSelected(isNewTheater);
        formPanel.add(generateSeatsCheckBox, gbc);

        // Seating Configuration Panel
        JPanel seatConfigPanel = new JPanel(new GridBagLayout());
        seatConfigPanel.setBorder(BorderFactory.createTitledBorder("Seating Configuration"));
        GridBagConstraints configGbc = new GridBagConstraints();
        configGbc.insets = new Insets(5, 5, 5, 5);
        configGbc.fill = GridBagConstraints.HORIZONTAL;

        // Rows
        configGbc.gridx = 0;
        configGbc.gridy = 0;
        seatConfigPanel.add(new JLabel("Number of Rows:"), configGbc);

        configGbc.gridx = 1;
        configGbc.gridy = 0;
        configGbc.weightx = 1.0;
        SpinnerNumberModel rowsModel = new SpinnerNumberModel(8, 1, 20, 1);
        rowsSpinner = new JSpinner(rowsModel);
        seatConfigPanel.add(rowsSpinner, configGbc);

        // Seats per Row
        configGbc.gridx = 0;
        configGbc.gridy = 1;
        configGbc.weightx = 0.0;
        seatConfigPanel.add(new JLabel("Seats per Row:"), configGbc);

        configGbc.gridx = 1;
        configGbc.gridy = 1;
        configGbc.weightx = 1.0;
        SpinnerNumberModel seatsModel = new SpinnerNumberModel(10, 1, 20, 1);
        seatsPerRowSpinner = new JSpinner(seatsModel);
        seatConfigPanel.add(seatsPerRowSpinner, configGbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        formPanel.add(seatConfigPanel, gbc);

        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveTheater());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Fill form with theater data if editing
        if (!isNewTheater) {
            populateForm();
        }

        add(mainPanel);

        // Add listener to enable/disable seating configuration
        generateSeatsCheckBox.addActionListener(e -> {
            boolean generateSeats = generateSeatsCheckBox.isSelected();
            rowsSpinner.setEnabled(generateSeats);
            seatsPerRowSpinner.setEnabled(generateSeats);
        });

        // Set initial state of spinners
        rowsSpinner.setEnabled(generateSeatsCheckBox.isSelected());
        seatsPerRowSpinner.setEnabled(generateSeatsCheckBox.isSelected());
    }

    private void populateForm() {
        nameField.setText(theater.getTheaterName());
        locationField.setText(theater.getLocation());
        capacityField.setText(String.valueOf(theater.getCapacity()));
    }

    private void saveTheater() {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String capacityStr = capacityField.getText().trim();

        // Validation
        if (name.isEmpty() || location.isEmpty() || capacityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                throw new NumberFormatException("Capacity must be positive");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid capacity (positive number)",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (isNewTheater) {
                // Create new theater
                theaterService.addTheater(name, location, capacity);

                // Get the new theater to generate seats if needed
                if (generateSeatsCheckBox.isSelected()) {
                    // Find the newly created theater by name and location
                    List<Theater> theaters = theaterService.getAllTheaters();
                    for (Theater t : theaters) {
                        if (t.getTheaterName().equals(name) && t.getLocation().equals(location)) {
                            int rows = (Integer) rowsSpinner.getValue();
                            int seatsPerRow = (Integer) seatsPerRowSpinner.getValue();
                            theaterService.generateTheaterSeats(t.getTheaterId(), rows, seatsPerRow);
                            break;
                        }
                    }
                }

                JOptionPane.showMessageDialog(this,
                        "Theater added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing theater
                theaterService.updateTheater(theater.getTheaterId(), name, location, capacity);

                // Regenerate seats if requested
                if (generateSeatsCheckBox.isSelected()) {
                    int rows = (Integer) rowsSpinner.getValue();
                    int seatsPerRow = (Integer) seatsPerRowSpinner.getValue();
                    theaterService.generateTheaterSeats(theater.getTheaterId(), rows, seatsPerRow);
                }

                JOptionPane.showMessageDialog(this,
                        "Theater updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving theater: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
