package ru.cinema.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cinema.model.User;
import ru.cinema.repository.UserRepository;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }

    @PostMapping("/users/changeRole/{id}")
    public String changeRole(@PathVariable("id") Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getUsername().equals(currentUsername)) {
            return "redirect:/admin/users?error=cannotChangeYourRole";
        }

        if (user.getRole().equals("ADMIN")) {
            user.setRole("USER");
        } else {
            user.setRole("ADMIN");
        }

        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getUsername().equals(currentUsername)) {
            return "redirect:/admin/users?error=cannotDeleteYourself";
        }

        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/test/403")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String test403() {
        return "admin/test_403";
    }

    @GetMapping("/force403")
    public String force403() {
        throw new AccessDeniedException("Доступ запрещен! Тестовая ошибка 403");
    }

    @GetMapping("/test/500")
    public String test500() {
        throw new RuntimeException("Тестовая ошибка 500");
    }

    @GetMapping("/test/404")
    public String test404() {
        return "non_existing_template";
    }
}
