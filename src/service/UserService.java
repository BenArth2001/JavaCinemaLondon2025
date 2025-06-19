package service;

import dao.UserDAO;
import dao.UserRoleDAO;
import model.User;
import model.UserRole;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO;
    private UserRoleDAO userRoleDAO;
    private User currentUser;

    public UserService() {
        this.userDAO = new UserDAO();
        this.userRoleDAO = new UserRoleDAO();
    }

    public boolean login(String username, String password) throws SQLException {
        if (userDAO.authenticateUser(username, password)) {
            currentUser = userDAO.getUserByUsername(username);
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && currentUser.getRoleId() == 3; // Admin role ID is 3
    }

    public boolean isEmployee() {
        return isLoggedIn() && (currentUser.getRoleId() == 2 || currentUser.getRoleId() == 3); // Employee role ID is 2,
                                                                                               // Admin is 3
    }

    public boolean isCustomer() {
        return isLoggedIn() && currentUser.getRoleId() == 1; // Customer role ID is 1
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public User getUserById(int userId) throws SQLException {
        return userDAO.getUserById(userId);
    }

    public List<User> getUsersByRole(int roleId) throws SQLException {
        return userDAO.getUsersByRole(roleId);
    }

    public void registerCustomer(String username, String password, String firstName,
            String lastName, String email, String phone) throws SQLException {
        User user = new User(0, username, password, firstName, lastName, email, phone, 1, null);
        userDAO.addUser(user);
    }

    public void addEmployee(String username, String password, String firstName,
            String lastName, String email, String phone) throws SQLException {
        User user = new User(0, username, password, firstName, lastName, email, phone, 2, null);
        userDAO.addUser(user);
    }

    public void addAdmin(String username, String password, String firstName,
            String lastName, String email, String phone) throws SQLException {
        User user = new User(0, username, password, firstName, lastName, email, phone, 3, null);
        userDAO.addUser(user);
    }

    public void updateUser(int userId, String username, String password, String firstName,
            String lastName, String email, String phone, int roleId) throws SQLException {
        User user = new User(userId, username, password, firstName, lastName, email, phone, roleId, null);
        userDAO.updateUser(user);

        // Update current user if it's the same user
        if (currentUser != null && currentUser.getUserId() == userId) {
            currentUser = user;
        }
    }

    public void deleteUser(int userId) throws SQLException {
        userDAO.deleteUser(userId);

        // Logout if the deleted user is the current user
        if (currentUser != null && currentUser.getUserId() == userId) {
            logout();
        }
    }

    public List<UserRole> getAllUserRoles() throws SQLException {
        return userRoleDAO.getAllUserRoles();
    }

    public UserRole getUserRoleById(int roleId) throws SQLException {
        return userRoleDAO.getUserRoleById(roleId);
    }

    public String getUserRoleName(int roleId) throws SQLException {
        UserRole role = userRoleDAO.getUserRoleById(roleId);
        return role != null ? role.getRoleName() : "Unknown";
    }

    public boolean isUsernameAvailable(String username) throws SQLException {
        return userDAO.getUserByUsername(username) == null;
    }

    public User getUserByUsername(String username) throws SQLException {
        return userDAO.getUserByUsername(username);
    }
}
