package ui;

import model.*;
import service.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class PaymentScreen extends JFrame {
    private MainApplication mainApp;
    private int showingId;
    private List<Integer> selectedSeatIds;
    private Integer discountId;

    private ShowingService showingService;
    private TheaterService theaterService;
    private BookingService bookingService;
    private DiscountService discountService;

    private JTextField cardNumberField;
    private JTextField nameOnCardField;
    private JTextField expiryDateField;
    private JPasswordField cvvField;
    private JLabel totalAmountLabel;
    private BigDecimal totalAmount;

    public PaymentScreen(MainApplication mainApp, int showingId, List<Integer> selectedSeatIds, Integer discountId) {
        this.mainApp = mainApp;
        this.showingId = showingId;
        this.selectedSeatIds = selectedSeatIds;
        this.discountId = discountId;

        this.showingService = new ShowingService();
        this.theaterService = new TheaterService();
        this.bookingService = new BookingService();
        this.discountService = new DiscountService();

        // Set up the frame
        setTitle("Payment");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            calculateTotalAmount();
            initComponents();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error preparing payment: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose();
        }
    }

    private void calculateTotalAmount() throws SQLException {
        totalAmount = BigDecimal.ZERO;

        for (int seatId : selectedSeatIds) {
            Seat seat = theaterService.getSeatById(seatId);
            if (seat != null) {
                BigDecimal seatPrice = showingService.getShowingPrice(showingId, seat.getSeatType());
                totalAmount = totalAmount.add(seatPrice);
            }
        }

        // Apply discount if applicable
        if (discountId != null && discountId > 0) {
            totalAmount = discountService.calculateDiscountedPrice(totalAmount, discountId);
        }
    }

    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Booking summary panel
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Booking Summary"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel summaryDetailsPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        try {
            Movie movie = showingService.getMovieForShowing(showingId);
            Theater theater = showingService.getTheaterForShowing(showingId);
            Showing showing = showingService.getShowingById(showingId);

            summaryDetailsPanel.add(new JLabel("Movie:"));
            summaryDetailsPanel.add(new JLabel(movie != null ? movie.getTitle() : "Unknown"));

            summaryDetailsPanel.add(new JLabel("Theater:"));
            summaryDetailsPanel.add(new JLabel(theater != null ? theater.getTheaterName() : "Unknown"));

            summaryDetailsPanel.add(new JLabel("Date/Time:"));
            summaryDetailsPanel.add(
                    new JLabel(showing != null ? showing.getShowDate() + " at " + showing.getShowTime() : "Unknown"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        summaryPanel.add(summaryDetailsPanel, BorderLayout.NORTH);

        // Seats summary
        JPanel seatsPanel = new JPanel(new BorderLayout());
        seatsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        seatsPanel.add(new JLabel("Selected Seats:"), BorderLayout.NORTH);

        JPanel seatsList = new JPanel(new FlowLayout(FlowLayout.LEFT));
        try {
            for (int seatId : selectedSeatIds) {
                Seat seat = theaterService.getSeatById(seatId);
                if (seat != null) {
                    JLabel seatLabel = new JLabel(
                            seat.getSeatRow() + "" + seat.getSeatNumber() + " (" + seat.getSeatType() + ")");
                    seatLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                    seatsList.add(seatLabel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane seatsScrollPane = new JScrollPane(seatsList);
        seatsScrollPane.setPreferredSize(new Dimension(300, 60));
        seatsPanel.add(seatsScrollPane, BorderLayout.CENTER);

        summaryPanel.add(seatsPanel, BorderLayout.CENTER);

        // Total amount
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalAmountLabel = new JLabel("Total Amount: $" + totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountPanel.add(totalAmountLabel);

        summaryPanel.add(amountPanel, BorderLayout.SOUTH);

        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // Payment details panel
        JPanel paymentPanel = new JPanel(new BorderLayout());
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Payment Details"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel paymentDetailsPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        paymentDetailsPanel.add(new JLabel("Card Number:"));
        cardNumberField = new JTextField();
        paymentDetailsPanel.add(cardNumberField);

        paymentDetailsPanel.add(new JLabel("Name on Card:"));
        nameOnCardField = new JTextField();
        paymentDetailsPanel.add(nameOnCardField);

        paymentDetailsPanel.add(new JLabel("Expiry Date (MM/YY):"));
        expiryDateField = new JTextField();
        paymentDetailsPanel.add(expiryDateField);

        paymentDetailsPanel.add(new JLabel("CVV:"));
        cvvField = new JPasswordField();
        paymentDetailsPanel.add(cvvField);

        paymentPanel.add(paymentDetailsPanel, BorderLayout.CENTER);

        mainPanel.add(paymentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        JButton payButton = new JButton("Pay Now");
        payButton.addActionListener(e -> processPayment());
        buttonPanel.add(payButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void processPayment() {
        // Basic validation
        if (cardNumberField.getText().isEmpty() ||
                nameOnCardField.getText().isEmpty() ||
                expiryDateField.getText().isEmpty() ||
                cvvField.getPassword().length == 0) {

            JOptionPane.showMessageDialog(this,
                    "Please fill in all payment fields",
                    "Incomplete Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // This is a dummy payment processing - in a real application you'd integrate
        // with a payment gateway
        JOptionPane.showMessageDialog(this,
                "Processing payment...",
                "Payment",
                JOptionPane.INFORMATION_MESSAGE);

        try {
            // Create the booking
            int userId = mainApp.getUserService().getCurrentUser().getUserId();
            int bookingId = bookingService.createBooking(userId, showingId, selectedSeatIds, discountId);

            if (bookingId > 0) {
                JOptionPane.showMessageDialog(this,
                        "Payment successful! Your booking has been confirmed.\nBooking ID: " + bookingId,
                        "Booking Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);

                // Return to customer dashboard
                dispose();
                mainApp.showCustomerDashboard();
            } else {
                JOptionPane.showMessageDialog(this,
                        "There was a problem creating your booking. Please try again.",
                        "Booking Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error processing booking: " + e.getMessage(),
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
