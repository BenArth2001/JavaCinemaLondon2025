package ui;

import model.User;
import service.UserService;
import model.Movie;
import model.Genre;
import service.MovieService;
import model.Showing;
import service.ShowingService;
import model.Theater;
import service.TheaterService;
import model.DiscountType;
import service.DiscountService;
import model.report.*;
import service.ReportService;
import service.BookingService; // Add this import

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal; // Add this import for BigDecimal.ROUND_HALF_UP

public class AdminDashboard extends JFrame {
    private MainApplication mainApp;
    private User currentUser;
    private UserService userService;
    private MovieService movieService; // Add this field

    private JPanel mainPanel;
    private JPanel contentPanel;

    // Add a class-level field to store the JTabbedPane reference
    private JTabbedPane reportsTabs;

    public AdminDashboard(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.currentUser = mainApp.getUserService().getCurrentUser();
        this.userService = mainApp.getUserService();
        this.movieService = new MovieService(); // Initialize movie service

        // Set up the frame
        setTitle("Cinema Booking System - Admin Dashboard");
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
        headerPanel.setBackground(new Color(40, 40, 70));
        headerPanel.setPreferredSize(new Dimension(1000, 60));

        JLabel welcomeLabel = new JLabel(
                "Admin Dashboard - " + currentUser.getFirstName() + " " + currentUser.getLastName());
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
        sidebarPanel.setBackground(new Color(230, 230, 240));
        sidebarPanel.setPreferredSize(new Dimension(200, 700));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Admin has all the employee options plus user management
        JButton manageUsersButton = createSidebarButton("Manage Users");
        manageUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showManageUsersPanel();
            }
        });
        sidebarPanel.add(manageUsersButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

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

        JButton viewReportsButton = createSidebarButton("View Reports");
        viewReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReportsPanel();
            }
        });
        sidebarPanel.add(viewReportsButton);

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

        JLabel welcomeLabel = new JLabel("Welcome to the Admin Dashboard");
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

    private void showManageUsersPanel() {
        contentPanel.removeAll();

        JPanel manageUsersPanel = new JPanel(new BorderLayout());
        manageUsersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        manageUsersPanel.add(titleLabel, BorderLayout.NORTH);

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by role:"));

        JComboBox<String> roleFilter = new JComboBox<>(new String[] {
                "All Users", "Customers", "Employees", "Administrators"
        });

        filterPanel.add(roleFilter);

        JButton refreshButton = new JButton("Refresh");
        filterPanel.add(refreshButton);

        manageUsersPanel.add(filterPanel, BorderLayout.NORTH);

        // Create users table
        String[] columnNames = { "ID", "Username", "First Name", "Last Name", "Email", "Role", "Actions" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only allow editing the Actions column
            }
        };

        JTable usersTable = new JTable(model);
        usersTable.setRowHeight(35);
        usersTable.getTableHeader().setReorderingAllowed(false);

        // Set up action buttons column
        usersTable.getColumnModel().getColumn(6).setCellRenderer(new TableButtonRenderer());
        usersTable.getColumnModel().getColumn(6).setCellEditor(
                new TableButtonEditor(new JCheckBox(), (e, row) -> handleUserAction(e, row, usersTable)));

        // Set column widths
        usersTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        usersTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        usersTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        usersTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        usersTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        usersTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        manageUsersPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addUserButton = new JButton("Add New User");
        buttonPanel.add(addUserButton);
        manageUsersPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load user data
        loadUsersData(model, 0); // 0 means all users

        // Add listeners
        roleFilter.addActionListener(e -> {
            int selectedRole = roleFilter.getSelectedIndex();
            loadUsersData(model, selectedRole);
        });

        refreshButton.addActionListener(e -> {
            int selectedRole = roleFilter.getSelectedIndex();
            loadUsersData(model, selectedRole);
        });

        addUserButton.addActionListener(e -> {
            UserFormDialog dialog = new UserFormDialog(this, userService, null);
            dialog.setVisible(true);
            loadUsersData(model, roleFilter.getSelectedIndex());
        });

        contentPanel.add(manageUsersPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadUsersData(DefaultTableModel model, int roleFilter) {
        model.setRowCount(0);

        try {
            List<User> users;

            if (roleFilter == 0) {
                users = userService.getAllUsers();
            } else {
                users = userService.getUsersByRole(roleFilter);
            }

            for (User user : users) {
                String roleName = "Unknown";
                try {
                    roleName = userService.getUserRoleName(user.getRoleId());
                } catch (SQLException ex) {
                    // Ignore and use default
                }

                model.addRow(new Object[] {
                        user.getUserId(),
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        roleName,
                        "Edit/Delete"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading users: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleUserAction(ActionEvent e, int row, JTable table) {
        int userId = (int) table.getValueAt(row, 0);
        String actionCommand = e.getActionCommand();

        try {
            User user = userService.getUserById(userId);

            if (user == null) {
                JOptionPane.showMessageDialog(this,
                        "User not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("Edit".equals(actionCommand)) {
                UserFormDialog dialog = new UserFormDialog(this, userService, user);
                dialog.setVisible(true);

                // Refresh the table
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                loadUsersData(model, 0);
            } else if ("Delete".equals(actionCommand)) {
                // Don't allow deleting yourself
                if (user.getUserId() == currentUser.getUserId()) {
                    JOptionPane.showMessageDialog(this,
                            "You cannot delete your own account",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete user " + user.getUsername() + "?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    userService.deleteUser(userId);

                    // Refresh the table
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    loadUsersData(model, 0);

                    JOptionPane.showMessageDialog(this,
                            "User deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error processing user action: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showManageMoviesPanel() {
        contentPanel.removeAll();

        JPanel manageMoviesPanel = new JPanel(new BorderLayout());
        manageMoviesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Movies");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by genre:"));

        JComboBox<Genre> genreFilter = new JComboBox<>();
        genreFilter.addItem(new Genre(0, "All Genres"));

        try {
            List<Genre> genres = movieService.getAllGenres();
            for (Genre genre : genres) {
                genreFilter.addItem(genre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        filterPanel.add(genreFilter);

        JCheckBox showInactiveCheckbox = new JCheckBox("Show Inactive Movies");
        filterPanel.add(showInactiveCheckbox);

        JButton refreshButton = new JButton("Refresh");
        filterPanel.add(refreshButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        manageMoviesPanel.add(headerPanel, BorderLayout.NORTH);

        // Create movies table
        String[] columnNames = { "ID", "Title", "Genre", "Duration", "Rating", "Status", "Actions" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only allow editing the Actions column
            }
        };

        JTable moviesTable = new JTable(model);
        moviesTable.setRowHeight(35);
        moviesTable.getTableHeader().setReorderingAllowed(false);

        // Set up action buttons column
        moviesTable.getColumnModel().getColumn(6).setCellRenderer(new TableButtonRenderer());
        moviesTable.getColumnModel().getColumn(6).setCellEditor(
                new TableButtonEditor(new JCheckBox(), (e, row) -> handleMovieAction(e, row, moviesTable)));

        // Set column widths
        moviesTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        moviesTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        moviesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        moviesTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        moviesTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        moviesTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        moviesTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(moviesTable);
        manageMoviesPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addMovieButton = new JButton("Add New Movie");
        buttonPanel.add(addMovieButton);
        manageMoviesPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load movie data
        loadMoviesData(model, 0, showInactiveCheckbox.isSelected());

        // Add listeners
        genreFilter.addActionListener(e -> {
            Genre selectedGenre = (Genre) genreFilter.getSelectedItem();
            int genreId = selectedGenre.getGenreId();
            loadMoviesData(model, genreId, showInactiveCheckbox.isSelected());
        });

        showInactiveCheckbox.addActionListener(e -> {
            Genre selectedGenre = (Genre) genreFilter.getSelectedItem();
            int genreId = selectedGenre.getGenreId();
            loadMoviesData(model, genreId, showInactiveCheckbox.isSelected());
        });

        refreshButton.addActionListener(e -> {
            Genre selectedGenre = (Genre) genreFilter.getSelectedItem();
            int genreId = selectedGenre.getGenreId();
            loadMoviesData(model, genreId, showInactiveCheckbox.isSelected());
        });

        addMovieButton.addActionListener(e -> {
            MovieFormDialog dialog = new MovieFormDialog(this, movieService, null);
            dialog.setVisible(true);
            Genre selectedGenre = (Genre) genreFilter.getSelectedItem();
            int genreId = selectedGenre.getGenreId();
            loadMoviesData(model, genreId, showInactiveCheckbox.isSelected());
        });

        contentPanel.add(manageMoviesPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadMoviesData(DefaultTableModel model, int genreId, boolean showInactive) {
        model.setRowCount(0);

        try {
            List<Movie> movies;

            if (genreId == 0) {
                movies = showInactive ? movieService.getAllMovies() : movieService.getActiveMovies();
            } else {
                movies = movieService.getMoviesByGenre(genreId);

                if (!showInactive) {
                    movies = movies.stream()
                            .filter(Movie::isActive)
                            .collect(Collectors.toList());
                }
            }

            for (Movie movie : movies) {
                String genreName = "Unknown";
                try {
                    genreName = movieService.getMovieGenreName(movie.getGenreId());
                } catch (SQLException ex) {
                    // Ignore and use default
                }

                model.addRow(new Object[] {
                        movie.getMovieId(),
                        movie.getTitle(),
                        genreName,
                        movie.getDurationMinutes() + " min",
                        movie.getRating(),
                        movie.isActive() ? "Active" : "Inactive",
                        "Edit/Delete"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading movies: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleMovieAction(ActionEvent e, int row, JTable table) {
        int movieId = (int) table.getValueAt(row, 0);
        String actionCommand = e.getActionCommand();

        try {
            Movie movie = movieService.getMovieById(movieId);

            if (movie == null) {
                JOptionPane.showMessageDialog(this,
                        "Movie not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("Edit".equals(actionCommand)) {
                MovieFormDialog dialog = new MovieFormDialog(this, movieService, movie);
                dialog.setVisible(true);

                // Refresh the table
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                loadMoviesData(model, 0, true); // Reload all movies
            } else if ("Delete".equals(actionCommand)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete movie \"" + movie.getTitle() + "\"?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    movieService.deleteMovie(movieId);

                    // Refresh the table
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    loadMoviesData(model, 0, true); // Reload all movies

                    JOptionPane.showMessageDialog(this,
                            "Movie deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error processing movie action: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showManageShowingsPanel() {
        contentPanel.removeAll();

        JPanel manageShowingsPanel = new JPanel(new BorderLayout());
        manageShowingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Showings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Movie filter
        filterPanel.add(new JLabel("Movie:"));
        JComboBox<Movie> movieFilter = new JComboBox<>();
        movieFilter.addItem(new Movie(0, "All Movies"));

        try {
            List<Movie> movies = movieService.getAllMovies();
            for (Movie movie : movies) {
                movieFilter.addItem(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        filterPanel.add(movieFilter);

        // Theater filter
        filterPanel.add(new JLabel("Theater:"));
        JComboBox<Theater> theaterFilter = new JComboBox<>();
        theaterFilter.addItem(new Theater(0, "All Theaters", "", 0));

        try {
            TheaterService theaterService = new TheaterService();
            List<Theater> theaters = theaterService.getAllTheaters();
            for (Theater theater : theaters) {
                theaterFilter.addItem(theater);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        filterPanel.add(theaterFilter);

        // Date filter
        filterPanel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField(10);
        filterPanel.add(dateField);

        JButton applyFilterButton = new JButton("Apply Filter");
        filterPanel.add(applyFilterButton);

        JButton refreshButton = new JButton("Refresh");
        filterPanel.add(refreshButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        manageShowingsPanel.add(headerPanel, BorderLayout.NORTH);

        // Create showings table
        String[] columnNames = { "ID", "Movie", "Theater", "Date", "Time", "Base Price", "Actions" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only allow editing the Actions column
            }
        };

        JTable showingsTable = new JTable(model);
        showingsTable.setRowHeight(35);
        showingsTable.getTableHeader().setReorderingAllowed(false);

        // Set up action buttons column
        showingsTable.getColumnModel().getColumn(6).setCellRenderer(new TableButtonRenderer());
        showingsTable.getColumnModel().getColumn(6).setCellEditor(
                new TableButtonEditor(new JCheckBox(), (e, row) -> handleShowingAction(e, row, showingsTable)));

        // Set column widths
        showingsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        showingsTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        showingsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        showingsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        showingsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        showingsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        showingsTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(showingsTable);
        manageShowingsPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addShowingButton = new JButton("Add New Showing");
        buttonPanel.add(addShowingButton);
        manageShowingsPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load showing data
        ShowingService showingService = new ShowingService();
        loadShowingsData(model, 0, 0, null);

        // Add listeners
        applyFilterButton.addActionListener(e -> {
            Movie selectedMovie = (Movie) movieFilter.getSelectedItem();
            Theater selectedTheater = (Theater) theaterFilter.getSelectedItem();

            int movieId = selectedMovie.getMovieId();
            int theaterId = selectedTheater.getTheaterId();

            java.sql.Date date = null;
            if (!dateField.getText().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsed = sdf.parse(dateField.getText());
                    date = new java.sql.Date(parsed.getTime());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid date format. Please use yyyy-MM-dd",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            loadShowingsData(model, movieId, theaterId, date);
        });

        refreshButton.addActionListener(e -> {
            // Reset filters
            movieFilter.setSelectedIndex(0);
            theaterFilter.setSelectedIndex(0);
            dateField.setText("");

            // Reload all showings
            loadShowingsData(model, 0, 0, null);
        });

        addShowingButton.addActionListener(e -> {
            ShowingFormDialog dialog = new ShowingFormDialog(this, showingService, null);
            dialog.setVisible(true);
            loadShowingsData(model, 0, 0, null); // Refresh the table
        });

        contentPanel.add(manageShowingsPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadShowingsData(DefaultTableModel model, int movieId, int theaterId, java.sql.Date date) {
        model.setRowCount(0);

        try {
            ShowingService showingService = new ShowingService();
            List<Showing> showings;

            if (movieId > 0 && theaterId > 0 && date != null) {
                // Filter by all criteria
                showings = showingService.getShowingsByAll(movieId, theaterId, date);
            } else if (movieId > 0 && theaterId > 0) {
                // Filter by movie and theater
                showings = showingService.getShowingsByMovieAndTheater(movieId, theaterId);
            } else if (movieId > 0 && date != null) {
                // Filter by movie and date
                showings = showingService.getShowingsByMovieAndDate(movieId, date);
            } else if (theaterId > 0 && date != null) {
                // Filter by theater and date
                showings = showingService.getShowingsByTheaterAndDate(theaterId, date);
            } else if (movieId > 0) {
                // Filter by movie only
                showings = showingService.getShowingsByMovie(movieId);
            } else if (theaterId > 0) {
                // Filter by theater only
                showings = showingService.getShowingsByTheater(theaterId);
            } else if (date != null) {
                // Filter by date only
                showings = showingService.getShowingsByDate(date);
            } else {
                // No filters, get all
                showings = showingService.getAllShowings();
            }

            for (Showing showing : showings) {
                Movie movie = showingService.getMovieForShowing(showing.getShowingId());
                Theater theater = showingService.getTheaterForShowing(showing.getShowingId());

                model.addRow(new Object[] {
                        showing.getShowingId(),
                        movie != null ? movie.getTitle() : "Unknown",
                        theater != null ? theater.getTheaterName() : "Unknown",
                        showing.getShowDate(),
                        showing.getShowTime(),
                        "$" + showing.getBasePrice(),
                        "Edit/Delete"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading showings: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleShowingAction(ActionEvent e, int row, JTable table) {
        int showingId = (int) table.getValueAt(row, 0);
        String actionCommand = e.getActionCommand();

        try {
            ShowingService showingService = new ShowingService();
            Showing showing = showingService.getShowingById(showingId);

            if (showing == null) {
                JOptionPane.showMessageDialog(this,
                        "Showing not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("Edit".equals(actionCommand)) {
                ShowingFormDialog dialog = new ShowingFormDialog(this, showingService, showing);
                dialog.setVisible(true);

                // Refresh the table
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                loadShowingsData(model, 0, 0, null);
            } else if ("Delete".equals(actionCommand)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this showing?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    showingService.deleteShowing(showingId);

                    // Refresh the table
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    loadShowingsData(model, 0, 0, null);

                    JOptionPane.showMessageDialog(this,
                            "Showing deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error processing showing action: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showManageTheatersPanel() {
        contentPanel.removeAll();

        JPanel manageTheatersPanel = new JPanel(new BorderLayout());
        manageTheatersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Theaters");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Create filter and refresh panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Refresh");
        filterPanel.add(refreshButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        manageTheatersPanel.add(headerPanel, BorderLayout.NORTH);

        // Create theaters table
        String[] columnNames = { "ID", "Theater Name", "Location", "Capacity", "Seats", "Actions" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only allow editing the Actions column
            }
        };

        JTable theatersTable = new JTable(model);
        theatersTable.setRowHeight(35);
        theatersTable.getTableHeader().setReorderingAllowed(false);

        // Set up action buttons column
        theatersTable.getColumnModel().getColumn(5).setCellRenderer(new TableButtonRenderer());
        theatersTable.getColumnModel().getColumn(5).setCellEditor(
                new TableButtonEditor(new JCheckBox(), (e, row) -> handleTheaterAction(e, row, theatersTable)));

        // Set column widths
        theatersTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        theatersTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        theatersTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        theatersTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        theatersTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        theatersTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(theatersTable);
        manageTheatersPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addTheaterButton = new JButton("Add New Theater");
        buttonPanel.add(addTheaterButton);
        manageTheatersPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load theater data
        TheaterService theaterService = new TheaterService();
        loadTheatersData(model);

        // Add listeners
        refreshButton.addActionListener(e -> {
            loadTheatersData(model);
        });

        addTheaterButton.addActionListener(e -> {
            TheaterFormDialog dialog = new TheaterFormDialog(this, theaterService, null);
            dialog.setVisible(true);
            loadTheatersData(model);
        });

        contentPanel.add(manageTheatersPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadTheatersData(DefaultTableModel model) {
        model.setRowCount(0);

        try {
            TheaterService theaterService = new TheaterService();
            List<Theater> theaters = theaterService.getAllTheaters();

            for (Theater theater : theaters) {
                // Get seat count
                int seatCount = theaterService.getAllSeatsForTheater(theater.getTheaterId()).size();

                model.addRow(new Object[] {
                        theater.getTheaterId(),
                        theater.getTheaterName(),
                        theater.getLocation(),
                        theater.getCapacity(),
                        seatCount,
                        "Edit/Delete"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading theaters: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleTheaterAction(ActionEvent e, int row, JTable table) {
        int theaterId = (int) table.getValueAt(row, 0);
        String actionCommand = e.getActionCommand();

        try {
            TheaterService theaterService = new TheaterService();
            Theater theater = theaterService.getTheaterById(theaterId);

            if (theater == null) {
                JOptionPane.showMessageDialog(this,
                        "Theater not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("Edit".equals(actionCommand)) {
                TheaterFormDialog dialog = new TheaterFormDialog(this, theaterService, theater);
                dialog.setVisible(true);

                // Refresh the table
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                loadTheatersData(model);
            } else if ("Delete".equals(actionCommand)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete theater \"" + theater.getTheaterName() + "\"?\n" +
                                "This will also delete all seats and related showings.",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    theaterService.deleteTheater(theaterId);

                    // Refresh the table
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    loadTheatersData(model);

                    JOptionPane.showMessageDialog(this,
                            "Theater deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else if ("Seats".equals(actionCommand)) {
                // Show seats management
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Do you want to regenerate seats for this theater?\n" +
                                "This will remove all existing seats and create a new default layout.",
                        "Regenerate Seats",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    theaterService.generateTheaterSeats(theaterId, 8, 10); // Default 8 rows, 10 seats per row

                    // Refresh the table
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    loadTheatersData(model);

                    JOptionPane.showMessageDialog(this,
                            "Theater seats regenerated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error processing theater action: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showManageDiscountsPanel() {
        contentPanel.removeAll();

        JPanel manageDiscountsPanel = new JPanel(new BorderLayout());
        manageDiscountsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Discounts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JCheckBox showInactiveCheckbox = new JCheckBox("Show Inactive Discounts");
        filterPanel.add(showInactiveCheckbox);

        JButton refreshButton = new JButton("Refresh");
        filterPanel.add(refreshButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        manageDiscountsPanel.add(headerPanel, BorderLayout.NORTH);

        // Create discounts table
        String[] columnNames = { "ID", "Discount Name", "Description", "Percentage", "Status", "Actions" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only allow editing the Actions column
            }
        };

        JTable discountsTable = new JTable(model);
        discountsTable.setRowHeight(35);
        discountsTable.getTableHeader().setReorderingAllowed(false);

        // Set up action buttons column
        discountsTable.getColumnModel().getColumn(5).setCellRenderer(new TableButtonRenderer());
        discountsTable.getColumnModel().getColumn(5).setCellEditor(
                new TableButtonEditor(new JCheckBox(), (e, row) -> handleDiscountAction(e, row, discountsTable)));

        // Set column widths
        discountsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        discountsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        discountsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        discountsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        discountsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        discountsTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(discountsTable);
        manageDiscountsPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addDiscountButton = new JButton("Add New Discount");
        buttonPanel.add(addDiscountButton);
        manageDiscountsPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load discount data
        loadDiscountsData(model, showInactiveCheckbox.isSelected());

        // Add listeners
        showInactiveCheckbox.addActionListener(e -> {
            loadDiscountsData(model, showInactiveCheckbox.isSelected());
        });

        refreshButton.addActionListener(e -> {
            loadDiscountsData(model, showInactiveCheckbox.isSelected());
        });

        addDiscountButton.addActionListener(e -> {
            DiscountFormDialog dialog = new DiscountFormDialog(this, new DiscountService(), null);
            dialog.setVisible(true);
            loadDiscountsData(model, showInactiveCheckbox.isSelected());
        });

        contentPanel.add(manageDiscountsPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadDiscountsData(DefaultTableModel model, boolean showInactive) {
        model.setRowCount(0);

        try {
            DiscountService discountService = new DiscountService();
            List<DiscountType> discounts;

            if (showInactive) {
                discounts = discountService.getAllDiscountTypes();
            } else {
                discounts = discountService.getActiveDiscountTypes();
            }

            for (DiscountType discount : discounts) {
                model.addRow(new Object[] {
                        discount.getDiscountId(),
                        discount.getDiscountName(),
                        discount.getDiscountDescription(),
                        discount.getDiscountPercentage() + "%",
                        discount.isActive() ? "Active" : "Inactive",
                        "Edit/Delete"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading discounts: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleDiscountAction(ActionEvent e, int row, JTable table) {
        int discountId = (int) table.getValueAt(row, 0);
        String actionCommand = e.getActionCommand();

        try {
            DiscountService discountService = new DiscountService();
            DiscountType discount = discountService.getDiscountTypeById(discountId);

            if (discount == null) {
                JOptionPane.showMessageDialog(this,
                        "Discount not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("Edit".equals(actionCommand)) {
                DiscountFormDialog dialog = new DiscountFormDialog(this, discountService, discount);
                dialog.setVisible(true);

                // Refresh the table
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                loadDiscountsData(model, true);
            } else if ("Delete".equals(actionCommand)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete discount \"" + discount.getDiscountName() + "\"?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    discountService.deleteDiscountType(discountId);

                    // Refresh the table
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    loadDiscountsData(model, true);

                    JOptionPane.showMessageDialog(this,
                            "Discount deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else if ("Toggle".equals(actionCommand)) {
                if (discount.isActive()) {
                    discountService.deactivateDiscount(discountId);
                    JOptionPane.showMessageDialog(this,
                            "Discount deactivated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    discountService.activateDiscount(discountId);
                    JOptionPane.showMessageDialog(this,
                            "Discount activated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                // Refresh the table
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                loadDiscountsData(model, true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error processing discount action: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showReportsPanel() {
        contentPanel.removeAll();

        JPanel reportsPanel = new JPanel(new BorderLayout());
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with title and date range filter
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Reports Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Date range filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("From:"));
        JTextField fromDateField = new JTextField(10);
        filterPanel.add(fromDateField);

        filterPanel.add(new JLabel("To:"));
        JTextField toDateField = new JTextField(10);
        filterPanel.add(toDateField);

        JButton applyFilterButton = new JButton("Apply Filter");
        filterPanel.add(applyFilterButton);

        // Set default dates (last 30 days)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        toDateField.setText(dateFormat.format(calendar.getTime()));
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -30);
        fromDateField.setText(dateFormat.format(calendar.getTime()));

        headerPanel.add(filterPanel, BorderLayout.CENTER);
        reportsPanel.add(headerPanel, BorderLayout.NORTH);

        // Create a tabbed pane for different report types and store the reference
        reportsTabs = new JTabbedPane();

        // Add sales report panel
        JPanel salesReportPanel = createSalesReportPanel();
        reportsTabs.addTab("Sales Reports", salesReportPanel);

        // Add movie popularity panel
        JPanel moviePopularityPanel = createMoviePopularityPanel();
        reportsTabs.addTab("Movie Popularity", moviePopularityPanel);

        // Add theater utilization panel
        JPanel theaterUtilizationPanel = createTheaterUtilizationPanel();
        reportsTabs.addTab("Theater Utilization", theaterUtilizationPanel);

        // Add discount usage panel
        JPanel discountUsagePanel = createDiscountUsagePanel();
        reportsTabs.addTab("Discount Usage", discountUsagePanel);

        reportsPanel.add(reportsTabs, BorderLayout.CENTER);

        // Handle date filter
        applyFilterButton.addActionListener(e -> {
            try {
                java.util.Date fromDate = dateFormat.parse(fromDateField.getText());
                java.util.Date toDate = dateFormat.parse(toDateField.getText());

                // Apply the filter to each report
                refreshReports(new java.sql.Date(fromDate.getTime()), new java.sql.Date(toDate.getTime()));

            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please use yyyy-MM-dd",
                        "Date Format Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.add(reportsPanel, BorderLayout.CENTER);

        // Make sure UI is updated before loading data
        contentPanel.revalidate();
        contentPanel.repaint();

        // Initial load with default date range
        try {
            java.util.Date fromDate = dateFormat.parse(fromDateField.getText());
            java.util.Date toDate = dateFormat.parse(toDateField.getText());
            refreshReports(new java.sql.Date(fromDate.getTime()), new java.sql.Date(toDate.getTime()));
        } catch (ParseException ex) {
            // Should not happen with default dates
            ex.printStackTrace();
        }
    }

    private void refreshReports(java.sql.Date fromDate, java.sql.Date toDate) {
        try {
            // Use the stored reference instead of trying to find it
            if (reportsTabs == null) {
                throw new Exception("Reports tabs not initialized");
            }

            // Load sales report data (tab 0)
            if (reportsTabs.getTabCount() > 0) {
                Component salesTab = reportsTabs.getComponentAt(0);
                if (salesTab instanceof JPanel) {
                    loadSalesReportData((JPanel) salesTab, fromDate, toDate);
                }
            }

            // Load movie popularity data (tab 1)
            if (reportsTabs.getTabCount() > 1) {
                Component movieTab = reportsTabs.getComponentAt(1);
                if (movieTab instanceof JPanel) {
                    loadMoviePopularityData((JPanel) movieTab, fromDate, toDate);
                }
            }

            // Load theater utilization data (tab 2)
            if (reportsTabs.getTabCount() > 2) {
                Component theaterTab = reportsTabs.getComponentAt(2);
                if (theaterTab instanceof JPanel) {
                    loadTheaterUtilizationData((JPanel) theaterTab, fromDate, toDate);
                }
            }

            // Load discount usage data (tab 3)
            if (reportsTabs.getTabCount() > 3) {
                Component discountTab = reportsTabs.getComponentAt(3);
                if (discountTab instanceof JPanel) {
                    loadDiscountUsageData((JPanel) discountTab, fromDate, toDate);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading report data: " + e.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Summary panel for key metrics
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 0));

        // Add metric cards
        addMetricCard(summaryPanel, "Total Revenue", "$0.00", new Color(230, 240, 255));
        addMetricCard(summaryPanel, "Tickets Sold", "0", new Color(230, 255, 240));
        addMetricCard(summaryPanel, "Avg. Ticket Price", "$0.00", new Color(255, 240, 230));
        addMetricCard(summaryPanel, "Bookings", "0", new Color(240, 230, 255));

        panel.add(summaryPanel, BorderLayout.NORTH);

        // Table for detailed sales data
        String[] columnNames = { "Date", "Movie", "Theater", "Tickets Sold", "Revenue" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable salesTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(salesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Export button
        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportReportToCSV("sales_report"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMoviePopularityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table for movie popularity data
        String[] columnNames = { "Rank", "Movie", "Tickets Sold", "Revenue", "Showings", "Avg. Occupancy" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable movieTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(movieTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Export button
        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportReportToCSV("movie_popularity_report"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTheaterUtilizationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table for theater utilization data
        String[] columnNames = { "Theater", "Total Seats", "Seats Sold", "Utilization %", "Revenue" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable theaterTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(theaterTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Export button
        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportReportToCSV("theater_utilization_report"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDiscountUsagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table for discount usage data
        String[] columnNames = { "Discount", "Times Used", "Revenue Before", "Revenue After", "Savings" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable discountTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(discountTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Export button
        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportReportToCSV("discount_usage_report"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addMetricCard(JPanel panel, String title, String value, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setBackground(bgColor);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        card.add(valueLabel, BorderLayout.CENTER);

        panel.add(card);
    }

    private void loadSalesReportData(JPanel panel, java.sql.Date fromDate, java.sql.Date toDate) {
        try {
            // Get the metrics cards panel (first component)
            Component metricsComp = panel.getComponent(0);
            if (!(metricsComp instanceof JPanel)) {
                throw new Exception("Could not locate metrics panel in sales report");
            }
            JPanel metricsPanel = (JPanel) metricsComp;

            // Get the report data
            ReportService reportService = new ReportService();
            SalesReport salesReport = reportService.generateSalesReport(fromDate, toDate);

            // Update metrics - make sure we have at least 4 components in the metrics panel
            if (metricsPanel.getComponentCount() >= 4) {
                // Revenue card
                Component revenueComp = metricsPanel.getComponent(0);
                if (revenueComp instanceof JPanel) {
                    JPanel revenueCard = (JPanel) revenueComp;
                    if (revenueCard.getComponentCount() > 1 && revenueCard.getComponent(1) instanceof JLabel) {
                        ((JLabel) revenueCard.getComponent(1))
                                .setText("$" + salesReport.getTotalRevenue().setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }

                // Tickets card
                Component ticketsComp = metricsPanel.getComponent(1);
                if (ticketsComp instanceof JPanel) {
                    JPanel ticketsCard = (JPanel) ticketsComp;
                    if (ticketsCard.getComponentCount() > 1 && ticketsCard.getComponent(1) instanceof JLabel) {
                        ((JLabel) ticketsCard.getComponent(1)).setText(String.valueOf(salesReport.getTicketsSold()));
                    }
                }

                // Average price card
                Component avgPriceComp = metricsPanel.getComponent(2);
                if (avgPriceComp instanceof JPanel) {
                    JPanel avgPriceCard = (JPanel) avgPriceComp;
                    if (avgPriceCard.getComponentCount() > 1 && avgPriceCard.getComponent(1) instanceof JLabel) {
                        ((JLabel) avgPriceCard.getComponent(1))
                                .setText("$"
                                        + salesReport.getAverageTicketPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }

                // Bookings card
                Component bookingsComp = metricsPanel.getComponent(3);
                if (bookingsComp instanceof JPanel) {
                    JPanel bookingsCard = (JPanel) bookingsComp;
                    if (bookingsCard.getComponentCount() > 1 && bookingsCard.getComponent(1) instanceof JLabel) {
                        ((JLabel) bookingsCard.getComponent(1)).setText(String.valueOf(salesReport.getBookingCount()));
                    }
                }
            }

            // Update sales table
            Component scrollComp = null;
            if (panel.getComponentCount() > 1) {
                scrollComp = panel.getComponent(1);
            }

            if (scrollComp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) scrollComp;
                Component viewComp = scrollPane.getViewport().getView();

                if (viewComp instanceof JTable) {
                    JTable salesTable = (JTable) viewComp;
                    DefaultTableModel model = (DefaultTableModel) salesTable.getModel();

                    model.setRowCount(0);
                    for (SalesReportEntry entry : salesReport.getEntries()) {
                        model.addRow(new Object[] {
                                entry.getDate(),
                                entry.getMovieTitle(),
                                entry.getTheaterName(),
                                entry.getTicketsSold(),
                                "$" + entry.getRevenue().setScale(2, BigDecimal.ROUND_HALF_UP)
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading sales report data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMoviePopularityData(JPanel panel, java.sql.Date fromDate, java.sql.Date toDate) {
        try {
            // Get the report data
            ReportService reportService = new ReportService();
            MoviePopularityReport report = reportService.generateMoviePopularityReport(fromDate, toDate);

            // Update table
            JScrollPane scrollPane = (JScrollPane) panel.getComponent(0);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            model.setRowCount(0);
            int rank = 1;
            for (MoviePopularityEntry entry : report.getEntries()) {
                model.addRow(new Object[] {
                        rank++,
                        entry.getMovieTitle(),
                        entry.getTicketsSold(),
                        "$" + entry.getRevenue().setScale(2, BigDecimal.ROUND_HALF_UP),
                        entry.getShowingCount(),
                        entry.getAverageOccupancy() + "%"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTheaterUtilizationData(JPanel panel, java.sql.Date fromDate, java.sql.Date toDate) {
        try {
            // Get the report data
            ReportService reportService = new ReportService();
            TheaterUtilizationReport report = reportService.generateTheaterUtilizationReport(fromDate, toDate);

            // Update table
            JScrollPane scrollPane = (JScrollPane) panel.getComponent(0);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            model.setRowCount(0);
            for (TheaterUtilizationEntry entry : report.getEntries()) {
                model.addRow(new Object[] {
                        entry.getTheaterName(),
                        entry.getTotalSeats(),
                        entry.getSeatsSold(),
                        entry.getUtilizationPercentage() + "%",
                        "$" + entry.getRevenue().setScale(2, BigDecimal.ROUND_HALF_UP)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDiscountUsageData(JPanel panel, java.sql.Date fromDate, java.sql.Date toDate) {
        try {
            // Get the report data
            ReportService reportService = new ReportService();
            DiscountUsageReport report = reportService.generateDiscountUsageReport(fromDate, toDate);

            // Update table
            JScrollPane scrollPane = (JScrollPane) panel.getComponent(0);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            model.setRowCount(0);
            for (DiscountUsageEntry entry : report.getEntries()) {
                model.addRow(new Object[] {
                        entry.getDiscountName(),
                        entry.getTimesUsed(),
                        "$" + entry.getRevenueBeforeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP),
                        "$" + entry.getRevenueAfterDiscount().setScale(2, BigDecimal.ROUND_HALF_UP),
                        "$" + entry.getSavings().setScale(2, BigDecimal.ROUND_HALF_UP)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportReportToCSV(String reportName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report as CSV");
        fileChooser.setSelectedFile(new File(reportName + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try {
                // Get the currently selected tab using the stored reference
                int selectedIndex = reportsTabs.getSelectedIndex();
                JPanel selectedPanel = (JPanel) reportsTabs.getComponentAt(selectedIndex);

                // Get the table from the selected panel
                JScrollPane scrollPane = null;

                // Find the scroll pane in the panel
                for (Component comp : selectedPanel.getComponents()) {
                    if (comp instanceof JScrollPane) {
                        scrollPane = (JScrollPane) comp;
                        break;
                    }
                }

                if (scrollPane == null) {
                    throw new Exception("Could not locate scroll pane in report panel");
                }

                JTable table = (JTable) scrollPane.getViewport().getView();

                // Export the table data to CSV
                exportTableToCSV(table, fileToSave);

                JOptionPane.showMessageDialog(this,
                        "Report exported successfully to: " + fileToSave.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting report: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void exportTableToCSV(JTable table, File file) throws IOException {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        int colCount = model.getColumnCount();

        try (FileWriter writer = new FileWriter(file)) {
            // Write column headers
            for (int i = 0; i < colCount; i++) {
                writer.append(model.getColumnName(i));
                if (i < colCount - 1) {
                    writer.append(",");
                }
            }
            writer.append("\n");

            // Write data rows
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    Object value = model.getValueAt(row, col);
                    writer.append(value != null ? value.toString() : "");
                    if (col < colCount - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
        }
    }
}
