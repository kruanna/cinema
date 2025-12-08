package ru.cinema.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cinema.model.Session;
import ru.cinema.repository.MovieRepository;
import ru.cinema.repository.SessionRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class SessionController {

    private final SessionRepository sessionRepository;
    private final MovieRepository movieRepository;

    public SessionController(SessionRepository sessionRepository, MovieRepository movieRepository) {
        this.sessionRepository = sessionRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping("/sessions")
    public String listSessions(
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "sort", required = false) String sort,
            Model model) {

        List<Session> sessions;

        if (date != null && !date.isEmpty()) {
            sessions = sessionRepository.findByDate(LocalDate.parse(date));
        }
        else if (search != null && !search.isEmpty()) {
            sessions = sessionRepository.findByMovie_TitleContainingIgnoreCase(search);
        }
        else if ("time".equals(sort)) {
            sessions = sessionRepository.findAllByOrderByTimeAsc();
        }
        else {
            sessions = sessionRepository.findAll();
        }

        model.addAttribute("sessions", sessions);
        model.addAttribute("date", date);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        return "sessions";
    }

    @GetMapping("/sessions/add")
    public String addSessionForm(Model model) {
        model.addAttribute("session", new Session());
        model.addAttribute("movies", movieRepository.findAll());
        return "session_form";
    }

    @PostMapping("/sessions/save")
    public String saveSession(
            @Valid @ModelAttribute Session session,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("movies", movieRepository.findAll());
            return "session_form";
        }

        sessionRepository.save(session);

        model.addAttribute("success", "Сеанс успешно сохранён!");
        model.addAttribute("redirectUrl", "/sessions");

        return "session_form";
    }

    @GetMapping("/sessions/edit/{id}")
    public String editSession(@PathVariable("id") Long id, Model model) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сеанс не найден"));
        model.addAttribute("session", session);
        model.addAttribute("movies", movieRepository.findAll());
        return "session_form";
    }

    @GetMapping("/sessions/delete/{id}")
    public String deleteSession(@PathVariable("id") Long id) {
        sessionRepository.deleteById(id);
        return "redirect:/sessions";
    }
}
