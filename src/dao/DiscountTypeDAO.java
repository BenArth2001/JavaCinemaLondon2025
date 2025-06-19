package dao;

import database.DatabaseConnection;
import model.DiscountType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountTypeDAO {

    public List<DiscountType> getAllDiscountTypes() throws SQLException {
        List<DiscountType> discountTypes = new ArrayList<>();
        String query = "SELECT * FROM discount_types";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                discountTypes.add(extractDiscountTypeFromResultSet(resultSet));
            }
        }

        return discountTypes;
    }

    public List<DiscountType> getActiveDiscountTypes() throws SQLException {
        List<DiscountType> discountTypes = new ArrayList<>();
        String query = "SELECT * FROM discount_types WHERE is_active = 1";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                discountTypes.add(extractDiscountTypeFromResultSet(resultSet));
            }
        }

        return discountTypes;
    }

    public DiscountType getDiscountTypeById(int discountId) throws SQLException {
        String query = "SELECT * FROM discount_types WHERE discount_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, discountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractDiscountTypeFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    public void addDiscountType(DiscountType discountType) throws SQLException {
        String query = "INSERT INTO discount_types (discount_name, discount_description, " +
                "discount_percentage, is_active) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, discountType.getDiscountName());
            statement.setString(2, discountType.getDiscountDescription());
            statement.setBigDecimal(3, discountType.getDiscountPercentage());
            statement.setBoolean(4, discountType.isActive());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    discountType.setDiscountId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateDiscountType(DiscountType discountType) throws SQLException {
        String query = "UPDATE discount_types SET discount_name = ?, discount_description = ?, " +
                "discount_percentage = ?, is_active = ? WHERE discount_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, discountType.getDiscountName());
            statement.setString(2, discountType.getDiscountDescription());
            statement.setBigDecimal(3, discountType.getDiscountPercentage());
            statement.setBoolean(4, discountType.isActive());
            statement.setInt(5, discountType.getDiscountId());

            statement.executeUpdate();
        }
    }

    public void deleteDiscountType(int discountId) throws SQLException {
        String query = "DELETE FROM discount_types WHERE discount_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, discountId);
            statement.executeUpdate();
        }
    }

    private DiscountType extractDiscountTypeFromResultSet(ResultSet resultSet) throws SQLException {
        int discountId = resultSet.getInt("discount_id");
        String discountName = resultSet.getString("discount_name");
        String discountDescription = resultSet.getString("discount_description");
        BigDecimal discountPercentage = resultSet.getBigDecimal("discount_percentage");
        boolean isActive = resultSet.getBoolean("is_active");

        return new DiscountType(discountId, discountName, discountDescription,
                discountPercentage, isActive);
    }
}
