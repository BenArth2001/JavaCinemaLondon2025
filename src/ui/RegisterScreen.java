package ui;

import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegisterScreen extends JFrame {
    private MainApplication mainApp;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;

    public RegisterScreen(MainApplication mainApp) {
        this.mainApp = mainApp;

        // Set up the frame
        setTitle("Cinema Booking System - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);

        // Create panel with a nice background color
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title label
        JLabel titleLabel = new JLabel("Register New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 20, 5);
        panel.add(titleLabel, gbc);

        // Username
        addLabelAndField(panel, "Username:", usernameField = new JTextField(20), gbc, 1);

        // Password
        addLabelAndField(panel, "Password:", passwordField = new JPasswordField(20), gbc, 2);

        // Confirm Password
        addLabelAndField(panel, "Confirm Password:", confirmPasswordField = new JPasswordField(20), gbc, 3);

        // First Name
        addLabelAndField(panel, "First Name:", firstNameField = new JTextField(20), gbc, 4);

        // Last Name
        addLabelAndField(panel, "Last Name:", lastNameField = new JTextField(20), gbc, 5);

        // Email
        addLabelAndField(panel, "Email:", emailField = new JTextField(20), gbc, 6);

        // Phone
        addLabelAndField(panel, "Phone:", phoneField = new JTextField(20), gbc, 7);

        // Register button
        JButton registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        panel.add(registerButton, gbc);

        // Back button
        JButton backButton = new JButton("Back to Login");
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 5, 20, 5);
        panel.add(backButton, gbc);

        // Scroll pane in case the form gets too long
        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane);

        // Add action listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showLoginScreen();
            }
        });
    }

    private void addLabelAndField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
                lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simple email validation
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserService userService = mainApp.getUserService();

            // Check if username is available
            if (!userService.isUsernameAvailable(username)) {
                JOptionPane.showMessageDialog(this,
                        "Username already exists. Please choose a different username.",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Register the user as a customer (role_id = 1)
            userService.registerCustomer(username, password, firstName, lastName, email, phone);

            JOptionPane.showMessageDialog(this,
                    "Registration successful! You can now log in.",
                    "Registration Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Return to login screen
            mainApp.showLoginScreen();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
