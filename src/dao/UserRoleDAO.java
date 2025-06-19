package dao;

import database.DatabaseConnection;
import model.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRoleDAO {

    public List<UserRole> getAllUserRoles() throws SQLException {
        List<UserRole> userRoles = new ArrayList<>();
        String query = "SELECT * FROM user_roles";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int roleId = resultSet.getInt("role_id");
                String roleName = resultSet.getString("role_name");
                userRoles.add(new UserRole(roleId, roleName));
            }
        }

        return userRoles;
    }

    public UserRole getUserRoleById(int roleId) throws SQLException {
        String query = "SELECT * FROM user_roles WHERE role_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, roleId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String roleName = resultSet.getString("role_name");
                    return new UserRole(roleId, roleName);
                }
            }
        }

        return null;
    }

    public void addUserRole(UserRole userRole) throws SQLException {
        String query = "INSERT INTO user_roles (role_name) VALUES (?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, userRole.getRoleName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userRole.setRoleId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateUserRole(UserRole userRole) throws SQLException {
        String query = "UPDATE user_roles SET role_name = ? WHERE role_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userRole.getRoleName());
            statement.setInt(2, userRole.getRoleId());
            statement.executeUpdate();
        }
    }

    public void deleteUserRole(int roleId) throws SQLException {
        String query = "DELETE FROM user_roles WHERE role_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, roleId);
            statement.executeUpdate();
        }
    }
}
