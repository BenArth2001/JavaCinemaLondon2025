package model;

public class Theater {
    private int theaterId;
    private String theaterName;
    private String location;
    private int capacity;

    public Theater(int theaterId, String theaterName, String location, int capacity) {
        this.theaterId = theaterId;
        this.theaterName = theaterName;
        this.location = location;
        this.capacity = capacity;
    }

    public int getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(int theaterId) {
        this.theaterId = theaterId;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return theaterName + " (" + location + ")";
    }
}
