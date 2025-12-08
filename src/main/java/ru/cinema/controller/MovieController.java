package ru.cinema.controller;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cinema.model.Movie;
import ru.cinema.repository.MovieRepository;
import java.util.List;

@Controller
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping("/movies")
    public String listMovies(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "sort", required = false) String sort,
            Model model) {

        List<Movie> movies;

        if (search != null && !search.isEmpty()) {
            movies = movieRepository.findByTitleContainingIgnoreCase(search);
        }
        else if (genre != null && !genre.isEmpty()) {
            movies = movieRepository.findByGenreIgnoreCase(genre);
        }
        else if ("title".equals(sort)) {
            movies = movieRepository.findAllByOrderByTitleAsc();
        }
        else if ("duration".equals(sort)) {
            movies = movieRepository.findAllByOrderByDurationMinAsc();
        }
        else {
            movies = movieRepository.findAll();
        }

        model.addAttribute("search", search);
        model.addAttribute("genre", genre);
        model.addAttribute("sort", sort);
        model.addAttribute("movies", movies);

        return "movies";
    }

    @GetMapping("/movies/add")
    public String addMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "movie_form";
    }

    @PostMapping("/movies/save")
    public String saveMovie(
            @Valid @ModelAttribute Movie movie,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "movie_form";
        }

        movieRepository.save(movie);

        model.addAttribute("success", "Фильм успешно сохранён!");
        model.addAttribute("redirectUrl", "/movies");

        return "movie_form";
    }

    @GetMapping("/movies/edit/{id}")
    public String editMovie(@PathVariable("id") Long id, Model model) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Фильм не найден"));
        model.addAttribute("movie", movie);
        return "movie_form";
    }

    @GetMapping("/movies/delete/{id}")
    public String deleteMovie(@PathVariable("id") Long id) {
        movieRepository.deleteById(id);
        return "redirect:/movies";
    }
}

