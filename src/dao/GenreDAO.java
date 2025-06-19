package dao;

import database.DatabaseConnection;
import model.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {

    public List<Genre> getAllGenres() throws SQLException {
        List<Genre> genres = new ArrayList<>();
        String query = "SELECT * FROM genres";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int genreId = resultSet.getInt("genre_id");
                String genreName = resultSet.getString("genre_name");
                genres.add(new Genre(genreId, genreName));
            }
        }

        return genres;
    }

    public Genre getGenreById(int genreId) throws SQLException {
        String query = "SELECT * FROM genres WHERE genre_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, genreId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String genreName = resultSet.getString("genre_name");
                    return new Genre(genreId, genreName);
                }
            }
        }

        return null;
    }

    public void addGenre(Genre genre) throws SQLException {
        String query = "INSERT INTO genres (genre_name) VALUES (?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, genre.getGenreName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    genre.setGenreId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateGenre(Genre genre) throws SQLException {
        String query = "UPDATE genres SET genre_name = ? WHERE genre_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, genre.getGenreName());
            statement.setInt(2, genre.getGenreId());
            statement.executeUpdate();
        }
    }

    public void deleteGenre(int genreId) throws SQLException {
        String query = "DELETE FROM genres WHERE genre_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, genreId);
            statement.executeUpdate();
        }
    }
}
