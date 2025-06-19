package model;

import java.math.BigDecimal;

public class DiscountType {
    private int discountId;
    private String discountName;
    private String discountDescription;
    private BigDecimal discountPercentage;
    private boolean isActive;

    public DiscountType(int discountId, String discountName, String discountDescription,
            BigDecimal discountPercentage, boolean isActive) {
        this.discountId = discountId;
        this.discountName = discountName;
        this.discountDescription = discountDescription;
        this.discountPercentage = discountPercentage;
        this.isActive = isActive;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public void setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return discountName + " (" + discountPercentage + "%)";
    }

    public BigDecimal calculateDiscount(BigDecimal amount) {
        return amount.multiply(discountPercentage).divide(new BigDecimal("100"));
    }
}
