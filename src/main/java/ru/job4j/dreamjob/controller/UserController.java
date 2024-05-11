package ru.job4j.dreamjob.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        Optional<User> optionalUser = userService.save(user);
        if (optionalUser.isEmpty()) {
            model.addAttribute("message", "Пользователь с таким email уже существует");
            return "users/register";
        }
        return "redirect:/vacancies";
    }
}