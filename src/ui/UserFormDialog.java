package ui;

import model.User;
import model.UserRole;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserFormDialog extends JDialog {
    private User user;
    private UserService userService;
    private boolean isNewUser;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<UserRole> roleComboBox;

    public UserFormDialog(JFrame parent, UserService userService, User user) {
        super(parent, user == null ? "Add New User" : "Edit User", true);
        this.userService = userService;
        this.user = user;
        this.isNewUser = (user == null);

        setSize(500, 500);
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

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("First Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        firstNameField = new JTextField(20);
        formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Last Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        lastNameField = new JTextField(20);
        formPanel.add(lastNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        roleComboBox = new JComboBox<>();
        loadRoles();
        formPanel.add(roleComboBox, gbc);

        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveUser());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Fill form with user data if editing
        if (!isNewUser) {
            populateForm();
        }

        add(mainPanel);
    }

    private void loadRoles() {
        try {
            List<UserRole> roles = userService.getAllUserRoles();
            for (UserRole role : roles) {
                roleComboBox.addItem(role);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading roles: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void populateForm() {
        usernameField.setText(user.getUsername());
        // Don't populate password fields when editing
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());

        // Set selected role
        for (int i = 0; i < roleComboBox.getItemCount(); i++) {
            UserRole role = roleComboBox.getItemAt(i);
            if (role.getRoleId() == user.getRoleId()) {
                roleComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void saveUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        UserRole selectedRole = (UserRole) roleComboBox.getSelectedItem();

        // Validation
        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isNewUser && password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Password is required for new users",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Check if username already exists (for new users or when changing username)
            if (isNewUser || !username.equals(user.getUsername())) {
                if (!userService.isUsernameAvailable(username)) {
                    JOptionPane.showMessageDialog(this,
                            "Username already exists. Please choose a different username.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (isNewUser) {
                // Create new user
                userService.registerCustomer(username, password, firstName, lastName, email, phone);

                // Since registerCustomer always creates a Customer (role 1), update to correct
                // role if needed
                if (selectedRole != null && selectedRole.getRoleId() != 1) {
                    User newUser = userService.getUserByUsername(username);
                    userService.updateUser(newUser.getUserId(), username, password,
                            firstName, lastName, email, phone, selectedRole.getRoleId());
                }
            } else {
                // Update existing user
                int userId = user.getUserId();
                // Only update password if a new one is provided
                String updatedPassword = password.isEmpty() ? user.getPassword() : password;
                userService.updateUser(userId, username, updatedPassword, firstName, lastName,
                        email, phone, selectedRole.getRoleId());
            }

            JOptionPane.showMessageDialog(this,
                    "User saved successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving user: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
