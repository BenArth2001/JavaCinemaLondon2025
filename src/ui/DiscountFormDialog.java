package ui;

import model.DiscountType;
import service.DiscountService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class DiscountFormDialog extends JDialog {
    private DiscountType discount;
    private DiscountService discountService;
    private boolean isNewDiscount;

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField percentageField;
    private JCheckBox isActiveCheckBox;

    public DiscountFormDialog(JFrame parent, DiscountService discountService, DiscountType discount) {
        super(parent, discount == null ? "Add New Discount" : "Edit Discount", true);
        this.discountService = discountService;
        this.discount = discount;
        this.isNewDiscount = (discount == null);

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

        // Discount Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Discount Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        // Percentage
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Percentage (%):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        percentageField = new JTextField(10);
        formPanel.add(percentageField, gbc);

        // Is Active
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Active:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        isActiveCheckBox = new JCheckBox("Discount is currently active");
        isActiveCheckBox.setSelected(true); // Default to active
        formPanel.add(isActiveCheckBox, gbc);

        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveDiscount());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Fill form with discount data if editing
        if (!isNewDiscount) {
            populateForm();
        }

        add(mainPanel);
    }

    private void populateForm() {
        nameField.setText(discount.getDiscountName());
        descriptionArea.setText(discount.getDiscountDescription());
        percentageField.setText(discount.getDiscountPercentage().toString());
        isActiveCheckBox.setSelected(discount.isActive());
    }

    private void saveDiscount() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String percentageStr = percentageField.getText().trim();
        boolean isActive = isActiveCheckBox.isSelected();

        // Validation
        if (name.isEmpty() || percentageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal percentage;
        try {
            percentage = new BigDecimal(percentageStr);
            if (percentage.compareTo(BigDecimal.ZERO) <= 0 || percentage.compareTo(new BigDecimal("100")) > 0) {
                throw new NumberFormatException("Percentage must be between 0 and 100");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid percentage (0-100)",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (isNewDiscount) {
                // Create new discount
                discountService.addDiscountType(name, description, percentage, isActive);
                JOptionPane.showMessageDialog(this,
                        "Discount added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing discount
                discountService.updateDiscountType(discount.getDiscountId(), name, description, percentage, isActive);
                JOptionPane.showMessageDialog(this,
                        "Discount updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving discount: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
