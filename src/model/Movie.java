package model;

import java.util.Date;

public class Movie {
    private int movieId;
    private String title;
    private String description;
    private int durationMinutes;
    private Date releaseDate;
    private int genreId;
    private String rating;
    private String posterUrl;
    private boolean isActive;

    // Constructor with minimal fields
    public Movie(int movieId, String title) {
        this.movieId = movieId;
        this.title = title;
    }

    // Full constructor
    public Movie(int movieId, String title, String description, int durationMinutes,
            Date releaseDate, int genreId, String rating, String posterUrl, boolean isActive) {
        this.movieId = movieId;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.releaseDate = releaseDate;
        this.genreId = genreId;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.isActive = isActive;
    }

    // Getters and setters
    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", releaseDate=" + releaseDate +
                ", genreId=" + genreId +
                ", rating='" + rating + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    // For backward compatibility with existing code
    public int getId() {
        return movieId;
    }
}
