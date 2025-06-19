package model;

public class Seat {
    private int seatId;
    private int theaterId;
    private char seatRow;
    private int seatNumber;
    private String seatType;

    public Seat(int seatId, int theaterId, char seatRow, int seatNumber, String seatType) {
        this.seatId = seatId;
        this.theaterId = theaterId;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(int theaterId) {
        this.theaterId = theaterId;
    }

    public char getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(char seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    @Override
    public String toString() {
        return seatRow + "" + seatNumber + " (" + seatType + ")";
    }

    public String getLocation() {
        return "Row " + seatRow + ", Seat " + seatNumber;
    }
}
