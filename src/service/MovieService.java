package service;

import dao.MovieDAO;
import dao.GenreDAO;
import model.Movie;
import model.Genre;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieService {
    private MovieDAO movieDAO;
    private GenreDAO genreDAO;

    public MovieService() {
        this.movieDAO = new MovieDAO();
        this.genreDAO = new GenreDAO();
    }

    public List<Movie> getAllMovies() throws SQLException {
        return movieDAO.getAllMovies();
    }

    public List<Movie> getActiveMovies() throws SQLException {
        return movieDAO.getActiveMovies();
    }

    public Movie getMovieById(int movieId) throws SQLException {
        return movieDAO.getMovieById(movieId);
    }

    public List<Movie> getMoviesByGenre(int genreId) throws SQLException {
        return movieDAO.getMoviesByGenre(genreId);
    }

    public void addMovie(String title, String description, int durationMinutes,
            Date releaseDate, int genreId, String rating,
            String posterUrl, boolean isActive) throws SQLException {
        Movie movie = new Movie(0, title, description, durationMinutes,
                releaseDate, genreId, rating, posterUrl, isActive);
        movieDAO.addMovie(movie);
    }

    public void updateMovie(int movieId, String title, String description, int durationMinutes,
            Date releaseDate, int genreId, String rating,
            String posterUrl, boolean isActive) throws SQLException {
        Movie movie = new Movie(movieId, title, description, durationMinutes,
                releaseDate, genreId, rating, posterUrl, isActive);
        movieDAO.updateMovie(movie);
    }

    public void deleteMovie(int movieId) throws SQLException {
        movieDAO.deleteMovie(movieId);
    }

    public void activateMovie(int movieId) throws SQLException {
        Movie movie = movieDAO.getMovieById(movieId);
        if (movie != null) {
            movie.setActive(true);
            movieDAO.updateMovie(movie);
        }
    }

    public void deactivateMovie(int movieId) throws SQLException {
        Movie movie = movieDAO.getMovieById(movieId);
        if (movie != null) {
            movie.setActive(false);
            movieDAO.updateMovie(movie);
        }
    }

    public List<Genre> getAllGenres() throws SQLException {
        return genreDAO.getAllGenres();
    }

    public Genre getGenreById(int genreId) throws SQLException {
        return genreDAO.getGenreById(genreId);
    }

    public String getMovieGenreName(int movieId) throws SQLException {
        Movie movie = movieDAO.getMovieById(movieId);
        if (movie != null) {
            Genre genre = genreDAO.getGenreById(movie.getGenreId());
            if (genre != null) {
                return genre.getGenreName();
            }
        }
        return "Unknown";
    }

    public List<Movie> searchMovies(String searchTerm) throws SQLException {
        List<Movie> allMovies = movieDAO.getAllMovies();
        List<Movie> matchingMovies = new ArrayList<>();

        searchTerm = searchTerm.toLowerCase();

        for (Movie movie : allMovies) {
            if (movie.getTitle().toLowerCase().contains(searchTerm) ||
                    (movie.getDescription() != null && movie.getDescription().toLowerCase().contains(searchTerm))) {
                matchingMovies.add(movie);
            }
        }

        return matchingMovies;
    }
}
