package dao;

import database.DatabaseConnection;
import model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Movie movie = extractMovieFromResultSet(resultSet);
                movies.add(movie);
            }
        }

        return movies;
    }

    public Movie getMovieById(int movieId) throws SQLException {
        String query = "SELECT * FROM movies WHERE movie_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractMovieFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    public List<Movie> getMoviesByGenre(int genreId) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies WHERE genre_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, genreId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Movie movie = extractMovieFromResultSet(resultSet);
                    movies.add(movie);
                }
            }
        }

        return movies;
    }

    public List<Movie> getActiveMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies WHERE is_active = 1";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Movie movie = extractMovieFromResultSet(resultSet);
                movies.add(movie);
            }
        }

        return movies;
    }

    public void addMovie(Movie movie) throws SQLException {
        String query = "INSERT INTO movies (title, description, duration_minutes, release_date, " +
                "genre_id, rating, poster_url, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getDescription());
            statement.setInt(3, movie.getDurationMinutes());

            if (movie.getReleaseDate() != null) {
                statement.setDate(4, new java.sql.Date(movie.getReleaseDate().getTime()));
            } else {
                statement.setNull(4, Types.DATE);
            }

            statement.setInt(5, movie.getGenreId());
            statement.setString(6, movie.getRating());
            statement.setString(7, movie.getPosterUrl());
            statement.setBoolean(8, movie.isActive());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    movie.setMovieId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateMovie(Movie movie) throws SQLException {
        String query = "UPDATE movies SET title = ?, description = ?, duration_minutes = ?, " +
                "release_date = ?, genre_id = ?, rating = ?, poster_url = ?, is_active = ? " +
                "WHERE movie_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getDescription());
            statement.setInt(3, movie.getDurationMinutes());

            if (movie.getReleaseDate() != null) {
                statement.setDate(4, new java.sql.Date(movie.getReleaseDate().getTime()));
            } else {
                statement.setNull(4, Types.DATE);
            }

            statement.setInt(5, movie.getGenreId());
            statement.setString(6, movie.getRating());
            statement.setString(7, movie.getPosterUrl());
            statement.setBoolean(8, movie.isActive());
            statement.setInt(9, movie.getMovieId());

            statement.executeUpdate();
        }
    }

    public void deleteMovie(int movieId) throws SQLException {
        String query = "DELETE FROM movies WHERE movie_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, movieId);
            statement.executeUpdate();
        }
    }

    private Movie extractMovieFromResultSet(ResultSet resultSet) throws SQLException {
        int movieId = resultSet.getInt("movie_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        int durationMinutes = resultSet.getInt("duration_minutes");
        Date releaseDate = resultSet.getDate("release_date");
        int genreId = resultSet.getInt("genre_id");
        String rating = resultSet.getString("rating");
        String posterUrl = resultSet.getString("poster_url");
        boolean isActive = resultSet.getBoolean("is_active");

        return new Movie(movieId, title, description, durationMinutes, releaseDate,
                genreId, rating, posterUrl, isActive);
    }
}
