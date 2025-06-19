package model;

import java.math.BigDecimal;

public class Ticket {
    private int ticketId;
    private int bookingId;
    private int seatId;
    private BigDecimal price;

    public Ticket(int ticketId, int bookingId, int seatId, BigDecimal price) {
        this.ticketId = ticketId;
        this.bookingId = bookingId;
        this.seatId = seatId;
        this.price = price;
    }

    // Constructor without id for creating new tickets
    public Ticket(int bookingId, int seatId, BigDecimal price) {
        this.bookingId = bookingId;
        this.seatId = seatId;
        this.price = price;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", bookingId=" + bookingId +
                ", seatId=" + seatId +
                ", price=" + price +
                '}';
    }
}
