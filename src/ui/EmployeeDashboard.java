package ui;

import model.User;
import service.MovieService;
import service.ShowingService;
import service.TheaterService;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeDashboard extends JFrame {
    private MainApplication mainApp;
    private User currentUser;
    private MovieService movieService;
    private ShowingService showingService;
    private TheaterService theaterService;

    private JPanel mainPanel;
    private JPanel contentPanel;

    public EmployeeDashboard(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.currentUser = mainApp.getUserService().getCurrentUser();
        this.movieService = new MovieService();
        this.showingService = new ShowingService();
        this.theaterService = new TheaterService();

        // Set up the frame
        setTitle("Cinema Booking System - Employee Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create main panel
        mainPanel = new JPanel(new BorderLayout());

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Set default content
        showWelcomePanel();

        // Add main panel to frame
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 50, 80));
        headerPanel.setPreferredSize(new Dimension(1000, 60));

        JLabel welcomeLabel = new JLabel(
                "Employee Dashboard - " + currentUser.getFirstName() + " " + currentUser.getLastName());
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

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(240, 240, 245));
        sidebarPanel.setPreferredSize(new Dimension(200, 700));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton managemoviesButton = createSidebarButton("Manage Movies");
        managemoviesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showManageMoviesPanel();
            }
        });
        sidebarPanel.add(managemoviesButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton manageShowingsButton = createSidebarButton("Manage Showings");
        manageShowingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showManageShowingsPanel();
            }
        });
        sidebarPanel.add(manageShowingsButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton manageTheatersButton = createSidebarButton("Manage Theaters");
        manageTheatersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showManageTheatersPanel();
            }
        });
        sidebarPanel.add(manageTheatersButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton manageDiscountsButton = createSidebarButton("Manage Discounts");
        manageDiscountsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showManageDiscountsPanel();
            }
        });
        sidebarPanel.add(manageDiscountsButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton viewBookingsButton = createSidebarButton("View Bookings");
        viewBookingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showViewBookingsPanel();
            }
        });
        sidebarPanel.add(viewBookingsButton);

        return sidebarPanel;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        return button;
    }

    private void showWelcomePanel() {
        contentPanel.removeAll();

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel welcomeLabel = new JLabel("Welcome to the Employee Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel instructionLabel = new JLabel("Please select an option from the sidebar to get started.");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        welcomePanel.add(instructionLabel, BorderLayout.SOUTH);

        contentPanel.add(welcomePanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showManageMoviesPanel() {
        contentPanel.removeAll();

        JPanel manageMoviesPanel = new JPanel(new BorderLayout());
        manageMoviesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Movies");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        manageMoviesPanel.add(titleLabel, BorderLayout.NORTH);

        // Movie management functionality would be implemented here
        JLabel placeholderLabel = new JLabel("Movie management functionality will be implemented here");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        manageMoviesPanel.add(placeholderLabel, BorderLayout.CENTER);

        contentPanel.add(manageMoviesPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showManageShowingsPanel() {
        contentPanel.removeAll();

        JPanel manageShowingsPanel = new JPanel(new BorderLayout());
        manageShowingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Showings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        manageShowingsPanel.add(titleLabel, BorderLayout.NORTH);

        // Showing management functionality would be implemented here
        JLabel placeholderLabel = new JLabel("Showing management functionality will be implemented here");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        manageShowingsPanel.add(placeholderLabel, BorderLayout.CENTER);

        contentPanel.add(manageShowingsPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showManageTheatersPanel() {
        // Similar to other panels - would show theater management UI
        contentPanel.removeAll();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Theaters");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel placeholderLabel = new JLabel("Theater management functionality will be implemented here");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(placeholderLabel, BorderLayout.CENTER);

        contentPanel.add(panel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showManageDiscountsPanel() {
        // Similar to other panels - would show discount management UI
        contentPanel.removeAll();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Discounts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel placeholderLabel = new JLabel("Discount management functionality will be implemented here");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(placeholderLabel, BorderLayout.CENTER);

        contentPanel.add(panel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showViewBookingsPanel() {
        // Similar to other panels - would show bookings view UI
        contentPanel.removeAll();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("View Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel placeholderLabel = new JLabel("Booking view functionality will be implemented here");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(placeholderLabel, BorderLayout.CENTER);

        contentPanel.add(panel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
