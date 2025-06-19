package service;

import dao.DiscountTypeDAO;
import model.DiscountType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class DiscountService {
    private DiscountTypeDAO discountTypeDAO;

    public DiscountService() {
        this.discountTypeDAO = new DiscountTypeDAO();
    }

    public List<DiscountType> getAllDiscountTypes() throws SQLException {
        return discountTypeDAO.getAllDiscountTypes();
    }

    public List<DiscountType> getActiveDiscountTypes() throws SQLException {
        return discountTypeDAO.getActiveDiscountTypes();
    }

    public DiscountType getDiscountTypeById(int discountId) throws SQLException {
        return discountTypeDAO.getDiscountTypeById(discountId);
    }

    public void addDiscountType(String name, String description, BigDecimal percentage, boolean isActive)
            throws SQLException {
        DiscountType discountType = new DiscountType(0, name, description, percentage, isActive);
        discountTypeDAO.addDiscountType(discountType);
    }

    public void updateDiscountType(int discountId, String name, String description, BigDecimal percentage,
            boolean isActive) throws SQLException {
        DiscountType discountType = new DiscountType(discountId, name, description, percentage, isActive);
        discountTypeDAO.updateDiscountType(discountType);
    }

    public void deleteDiscountType(int discountId) throws SQLException {
        discountTypeDAO.deleteDiscountType(discountId);
    }

    public void activateDiscount(int discountId) throws SQLException {
        DiscountType discountType = discountTypeDAO.getDiscountTypeById(discountId);
        if (discountType != null) {
            discountType.setActive(true);
            discountTypeDAO.updateDiscountType(discountType);
        }
    }

    public void deactivateDiscount(int discountId) throws SQLException {
        DiscountType discountType = discountTypeDAO.getDiscountTypeById(discountId);
        if (discountType != null) {
            discountType.setActive(false);
            discountTypeDAO.updateDiscountType(discountType);
        }
    }

    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, int discountId) throws SQLException {
        if (discountId <= 0) {
            return originalPrice;
        }

        DiscountType discountType = discountTypeDAO.getDiscountTypeById(discountId);
        if (discountType != null && discountType.isActive()) {
            BigDecimal discountAmount = originalPrice.multiply(discountType.getDiscountPercentage())
                    .divide(new BigDecimal("100"));
            return originalPrice.subtract(discountAmount);
        }

        return originalPrice;
    }

    public BigDecimal calculateDiscountAmount(BigDecimal originalPrice, int discountId) throws SQLException {
        if (discountId <= 0) {
            return BigDecimal.ZERO;
        }

        DiscountType discountType = discountTypeDAO.getDiscountTypeById(discountId);
        if (discountType != null && discountType.isActive()) {
            return originalPrice.multiply(discountType.getDiscountPercentage())
                    .divide(new BigDecimal("100"));
        }

        return BigDecimal.ZERO;
    }
}
