package ru.cinema.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.cinema.repository.MovieRepository;
import ru.cinema.repository.SessionRepository;

import java.util.List;

@Controller
public class StatisticsController {

    private final MovieRepository movieRepository;
    private final SessionRepository sessionRepository;

    public StatisticsController(MovieRepository movieRepository,
                                SessionRepository sessionRepository) {
        this.movieRepository = movieRepository;
        this.sessionRepository = sessionRepository;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/statistics")
    public String statistics(Model model) {

        long movieCount = movieRepository.count();
        long sessionCount = sessionRepository.count();

        // --- самый популярный жанр ---
        List<Object[]> genreStats = movieRepository.getGenreStats();

        String topGenre = "Нет данных";

        if (!genreStats.isEmpty()) {
            topGenre = (String) genreStats.get(0)[0];
        }

// создаём списки для диаграммы
        List<String> genres = new java.util.ArrayList<>();
        List<Long> counts = new java.util.ArrayList<>();

        for (Object[] row : genreStats) {
            genres.add((String) row[0]);
            counts.add((Long) row[1]);
        }

        model.addAttribute("genres", genres);
        model.addAttribute("counts", counts);

        // --- выручка ---
        Double totalRevenue = sessionRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = 0.0;

        model.addAttribute("movieCount", movieCount);
        model.addAttribute("sessionCount", sessionCount);
        model.addAttribute("topGenre", topGenre);
        model.addAttribute("totalRevenue", totalRevenue);

        return "statistics";
    }
}
