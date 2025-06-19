package model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SalesReport {
    private List<SalesReportEntry> entries = new ArrayList<>();
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private int ticketsSold = 0;
    private BigDecimal averageTicketPrice = BigDecimal.ZERO;
    private int bookingCount = 0;

    public List<SalesReportEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<SalesReportEntry> entries) {
        this.entries = entries;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public BigDecimal getAverageTicketPrice() {
        return averageTicketPrice;
    }

    public void setAverageTicketPrice(BigDecimal averageTicketPrice) {
        this.averageTicketPrice = averageTicketPrice;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(int bookingCount) {
        this.bookingCount = bookingCount;
    }
}
