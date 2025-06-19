package ui;

import model.*;
import service.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SeatSelectionScreen extends JFrame {
    private MainApplication mainApp;
    private int showingId;
    private Showing showing;
    private Movie movie;
    private Theater theater;

    private ShowingService showingService;
    private TheaterService theaterService;
    private BookingService bookingService;
    private DiscountService discountService;

    private JPanel seatsPanel;
    private Map<Integer, JToggleButton> seatButtons = new HashMap<>();
    private List<Integer> selectedSeatIds = new ArrayList<>();
    private JLabel totalPriceLabel;
    private JComboBox<DiscountType> discountComboBox;

    public SeatSelectionScreen(MainApplication mainApp, int showingId) {
        this.mainApp = mainApp;
        this.showingId = showingId;
        this.showingService = new ShowingService();
        this.theaterService = new TheaterService();
        this.bookingService = new BookingService();
        this.discountService = new DiscountService();

        // Set up the frame
        setTitle("Select Seats");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            // Load showing, movie and theater information
            showing = showingService.getShowingById(showingId);
            if (showing == null) {
                JOptionPane.showMessageDialog(this, "Showing not found", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            movie = showingService.getMovieForShowing(showingId);
            theater = showingService.getTheaterForShowing(showingId);

            initComponents();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading showing: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose();
        }
    }

    private void initComponents() throws SQLException {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top info panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Movie info
        JPanel moviePanel = new JPanel(new BorderLayout());
        moviePanel.setBorder(BorderFactory.createTitledBorder("Movie"));
        JLabel movieLabel = new JLabel(movie != null ? movie.getTitle() : "Unknown Movie");
        movieLabel.setFont(new Font("Arial", Font.BOLD, 14));
        moviePanel.add(movieLabel, BorderLayout.NORTH);
        if (movie != null && movie.getDurationMinutes() > 0) {
            JLabel durationLabel = new JLabel("Duration: " + movie.getDurationMinutes() + " min");
            moviePanel.add(durationLabel, BorderLayout.CENTER);
        }
        infoPanel.add(moviePanel);

        // Theater info
        JPanel theaterPanel = new JPanel(new BorderLayout());
        theaterPanel.setBorder(BorderFactory.createTitledBorder("Theater"));
        JLabel theaterLabel = new JLabel(theater != null ? theater.getTheaterName() : "Unknown Theater");
        theaterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        theaterPanel.add(theaterLabel, BorderLayout.NORTH);
        if (theater != null) {
            JLabel locationLabel = new JLabel("Location: " + theater.getLocation());
            theaterPanel.add(locationLabel, BorderLayout.CENTER);
        }
        infoPanel.add(theaterPanel);

        // Time info
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setBorder(BorderFactory.createTitledBorder("Showing"));
        String showingInfo = showing.getShowDate() + " at " + showing.getShowTime();
        JLabel timeLabel = new JLabel(showingInfo);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timePanel.add(timeLabel, BorderLayout.NORTH);
        JLabel priceLabel = new JLabel("Base price: $" + showing.getBasePrice());
        timePanel.add(priceLabel, BorderLayout.CENTER);
        infoPanel.add(timePanel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Seats panel (center)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Select Your Seats"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel screenLabel = new JLabel("SCREEN", JLabel.CENTER);
        screenLabel.setOpaque(true);
        screenLabel.setBackground(Color.LIGHT_GRAY);
        screenLabel.setPreferredSize(new Dimension(600, 30));
        screenLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel screenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        screenPanel.add(screenLabel);
        centerPanel.add(screenPanel, BorderLayout.NORTH);

        // Before loading the seat grid, ensure the theater has seats
        theaterService.ensureTheaterHasSeats(theater.getTheaterId());

        // Create the seat grid
        seatsPanel = new JPanel(new GridBagLayout());
        createSeatGrid();

        JScrollPane scrollPane = new JScrollPane(seatsPanel);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Legend panel
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));

        addLegendItem(legendPanel, Color.GREEN, "Available");
        addLegendItem(legendPanel, Color.RED, "Booked");
        addLegendItem(legendPanel, new Color(100, 150, 255), "Selected");

        centerPanel.add(legendPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for discount and checkout
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Discount panel
        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        discountPanel.add(new JLabel("Apply Discount:"));

        List<DiscountType> discounts = discountService.getActiveDiscountTypes();
        DiscountType[] discountArray = new DiscountType[discounts.size() + 1];
        discountArray[0] = new DiscountType(0, "No Discount", "No discount applied", BigDecimal.ZERO, true);
        for (int i = 0; i < discounts.size(); i++) {
            discountArray[i + 1] = discounts.get(i);
        }

        discountComboBox = new JComboBox<>(discountArray);
        discountComboBox.addActionListener(e -> updateTotalPrice());
        discountPanel.add(discountComboBox);

        bottomPanel.add(discountPanel, BorderLayout.WEST);

        // Price and checkout panel
        JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        totalPriceLabel = new JLabel("Total: $0.00");
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        checkoutPanel.add(totalPriceLabel);

        JButton proceedButton = new JButton("Proceed to Payment");
        proceedButton.addActionListener(e -> proceedToPayment());
        checkoutPanel.add(proceedButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        checkoutPanel.add(cancelButton);

        bottomPanel.add(checkoutPanel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Update the initial price
        updateTotalPrice();
    }

    private void addLegendItem(JPanel panel, Color color, String text) {
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(colorBox);
        panel.add(new JLabel(text));
    }

    private void createSeatGrid() throws SQLException {
        List<Seat> allSeats = theaterService.getAllSeatsForTheater(theater.getTheaterId());
        List<Seat> bookedSeats = bookingService.getBookedSeatsForShowing(showingId);

        if (allSeats.isEmpty()) {
            JLabel noSeatsLabel = new JLabel("No seats are configured for this theater.");
            noSeatsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            seatsPanel.add(noSeatsLabel);
            return;
        }

        // Group seats by row
        Map<Character, List<Seat>> seatsByRow = new HashMap<>();
        for (Seat seat : allSeats) {
            seatsByRow.computeIfAbsent(seat.getSeatRow(), k -> new ArrayList<>()).add(seat);
        }

        // Set of booked seat IDs for easy lookup
        Set<Integer> bookedSeatIds = bookedSeats.stream()
                .map(Seat::getSeatId)
                .collect(Collectors.toSet());

        // Sort rows
        List<Character> rows = new ArrayList<>(seatsByRow.keySet());
        Collections.sort(rows);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            char row = rows.get(rowIndex);
            List<Seat> seatsInRow = seatsByRow.get(row);

            // Sort seats by seat number
            Collections.sort(seatsInRow, Comparator.comparing(Seat::getSeatNumber));

            // Add row label
            gbc.gridx = 0;
            gbc.gridy = rowIndex;
            gbc.anchor = GridBagConstraints.CENTER;
            JLabel rowLabel = new JLabel(String.valueOf(row));
            rowLabel.setFont(new Font("Arial", Font.BOLD, 14));
            seatsPanel.add(rowLabel, gbc);

            // Add seats
            for (int seatIndex = 0; seatIndex < seatsInRow.size(); seatIndex++) {
                Seat seat = seatsInRow.get(seatIndex);
                gbc.gridx = seatIndex + 1;

                JToggleButton seatButton = new JToggleButton(String.valueOf(seat.getSeatNumber()));
                seatButton.setPreferredSize(new Dimension(40, 40));

                // Set the color based on seat type
                if ("premium".equals(seat.getSeatType())) {
                    seatButton.setBackground(new Color(255, 220, 220)); // Light red for premium
                } else if ("vip".equals(seat.getSeatType())) {
                    seatButton.setBackground(new Color(255, 255, 200)); // Light yellow for VIP
                } else {
                    seatButton.setBackground(new Color(220, 255, 220)); // Light green for standard
                }

                // Check if the seat is booked
                if (bookedSeatIds.contains(seat.getSeatId())) {
                    seatButton.setBackground(Color.RED);
                    seatButton.setEnabled(false);
                    seatButton.setText("X");
                }

                final int seatId = seat.getSeatId();
                seatButton.addActionListener(e -> {
                    if (seatButton.isSelected()) {
                        selectedSeatIds.add(seatId);
                        seatButton.setBackground(new Color(100, 150, 255)); // Blue when selected
                    } else {
                        selectedSeatIds.remove(Integer.valueOf(seatId));
                        // Restore original color based on seat type
                        if ("premium".equals(seat.getSeatType())) {
                            seatButton.setBackground(new Color(255, 220, 220));
                        } else if ("vip".equals(seat.getSeatType())) {
                            seatButton.setBackground(new Color(255, 255, 200));
                        } else {
                            seatButton.setBackground(new Color(220, 255, 220));
                        }
                    }
                    updateTotalPrice();
                });

                seatButtons.put(seat.getSeatId(), seatButton);
                seatsPanel.add(seatButton, gbc);
            }
        }
    }

    private void updateTotalPrice() {
        try {
            BigDecimal total = BigDecimal.ZERO;

            for (int seatId : selectedSeatIds) {
                Seat seat = theaterService.getSeatById(seatId);
                if (seat != null) {
                    BigDecimal seatPrice = showingService.getShowingPrice(showingId, seat.getSeatType());
                    total = total.add(seatPrice);
                }
            }

            // Apply discount if selected
            DiscountType selectedDiscount = (DiscountType) discountComboBox.getSelectedItem();
            if (selectedDiscount != null && selectedDiscount.getDiscountId() > 0) {
                total = discountService.calculateDiscountedPrice(total, selectedDiscount.getDiscountId());
            }

            totalPriceLabel.setText("Total: $" + total.setScale(2, BigDecimal.ROUND_HALF_UP));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error calculating price: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void proceedToPayment() {
        if (selectedSeatIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one seat", "No Seats Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        DiscountType selectedDiscount = (DiscountType) discountComboBox.getSelectedItem();
        Integer discountId = (selectedDiscount != null && selectedDiscount.getDiscountId() > 0)
                ? selectedDiscount.getDiscountId()
                : null;

        PaymentScreen paymentScreen = new PaymentScreen(mainApp, showingId, selectedSeatIds, discountId);
        paymentScreen.setVisible(true);
        dispose();
    }
}
