package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Booking {
    private int bookingId;
    private int userId;
    private int showingId;
    private Timestamp bookingDate;
    private BigDecimal totalAmount;
    private Integer discountId; // Can be null

    public Booking(int bookingId, int userId, int showingId, Timestamp bookingDate,
            BigDecimal totalAmount, Integer discountId) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.showingId = showingId;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
        this.discountId = discountId;
    }

    // Constructor without id for creating new bookings
    public Booking(int userId, int showingId, Timestamp bookingDate,
            BigDecimal totalAmount, Integer discountId) {
        this.userId = userId;
        this.showingId = showingId;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
        this.discountId = discountId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getShowingId() {
        return showingId;
    }

    public void setShowingId(int showingId) {
        this.showingId = showingId;
    }

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", showingId=" + showingId +
                ", bookingDate=" + bookingDate +
                ", totalAmount=" + totalAmount +
                ", discountId=" + discountId +
                '}';
    }
}
