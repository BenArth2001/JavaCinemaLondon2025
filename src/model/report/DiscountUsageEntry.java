package model.report;

import java.math.BigDecimal;

public class DiscountUsageEntry {
    private int discountId;
    private String discountName;
    private int timesUsed;
    private BigDecimal revenueBeforeDiscount = BigDecimal.ZERO;
    private BigDecimal revenueAfterDiscount = BigDecimal.ZERO;
    private BigDecimal savings = BigDecimal.ZERO;

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

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }

    public BigDecimal getRevenueBeforeDiscount() {
        return revenueBeforeDiscount;
    }

    public void setRevenueBeforeDiscount(BigDecimal revenueBeforeDiscount) {
        this.revenueBeforeDiscount = revenueBeforeDiscount;
    }

    public BigDecimal getRevenueAfterDiscount() {
        return revenueAfterDiscount;
    }

    public void setRevenueAfterDiscount(BigDecimal revenueAfterDiscount) {
        this.revenueAfterDiscount = revenueAfterDiscount;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }
}
