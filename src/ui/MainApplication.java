package ui;

import service.UserService;
import util.DatabaseInitializer;
import javax.swing.*;
import java.awt.*;

public class MainApplication {
    private JFrame currentFrame;
    private UserService userService;

    public MainApplication() {
        userService = new UserService();

        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize database with necessary data
        DatabaseInitializer.initialize();

        // Start with login screen
        showLoginScreen();
    }

    public void showLoginScreen() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }

        LoginScreen loginScreen = new LoginScreen(this);
        currentFrame = loginScreen;
        loginScreen.setVisible(true);
    }

    public void showCustomerDashboard() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }

        CustomerDashboard dashboard = new CustomerDashboard(this);
        currentFrame = dashboard;
        dashboard.setVisible(true);
    }

    public void showEmployeeDashboard() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }

        EmployeeDashboard dashboard = new EmployeeDashboard(this);
        currentFrame = dashboard;
        dashboard.setVisible(true);
    }

    public void showAdminDashboard() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }

        AdminDashboard dashboard = new AdminDashboard(this);
        currentFrame = dashboard;
        dashboard.setVisible(true);
    }

    public void showMovieBrowsingScreen() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }

        MovieBrowsingScreen screen = new MovieBrowsingScreen(this);
        currentFrame = screen;
        screen.setVisible(true);
    }

    public void showRegisterScreen() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }

        RegisterScreen screen = new RegisterScreen(this);
        currentFrame = screen;
        screen.setVisible(true);
    }

    public UserService getUserService() {
        return userService;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApplication();
        });
    }
}
