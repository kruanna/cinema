package ru.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.cinema.model.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByGenreIgnoreCase(String genre);

    List<Movie> findAllByOrderByTitleAsc();

    List<Movie> findAllByOrderByDurationMinAsc();

    @Query("SELECT m.genre, COUNT(m) FROM Movie m GROUP BY m.genre ORDER BY COUNT(m) DESC")
    List<Object[]> getGenreStats();

}
