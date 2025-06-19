package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

public class Showing {
    private int showingId;
    private int movieId;
    private int theaterId;
    private Date showDate;
    private Time showTime;
    private BigDecimal basePrice;

    public Showing(int showingId, int movieId, int theaterId, Date showDate, Time showTime, BigDecimal basePrice) {
        this.showingId = showingId;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.showDate = showDate;
        this.showTime = showTime;
        this.basePrice = basePrice;
    }

    public int getShowingId() {
        return showingId;
    }

    public void setShowingId(int showingId) {
        this.showingId = showingId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(int theaterId) {
        this.theaterId = theaterId;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public Time getShowTime() {
        return showTime;
    }

    public void setShowTime(Time showTime) {
        this.showTime = showTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public String toString() {
        return "Showing{" +
                "showingId=" + showingId +
                ", movieId=" + movieId +
                ", theaterId=" + theaterId +
                ", showDate=" + showDate +
                ", showTime=" + showTime +
                ", basePrice=" + basePrice +
                '}';
    }

    public String getFormattedDateTime() {
        return showDate + " at " + showTime;
    }
}
