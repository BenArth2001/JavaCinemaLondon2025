package model.report;

import java.math.BigDecimal;

public class MoviePopularityEntry {
    private int movieId;
    private String movieTitle;
    private int ticketsSold;
    private BigDecimal revenue;
    private int showingCount;
    private double averageOccupancy;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public int getShowingCount() {
        return showingCount;
    }

    public void setShowingCount(int showingCount) {
        this.showingCount = showingCount;
    }

    public double getAverageOccupancy() {
        return averageOccupancy;
    }

    public void setAverageOccupancy(double averageOccupancy) {
        this.averageOccupancy = averageOccupancy;
    }
}
