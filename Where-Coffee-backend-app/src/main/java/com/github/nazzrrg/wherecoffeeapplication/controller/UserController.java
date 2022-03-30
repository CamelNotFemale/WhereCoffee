package com.github.nazzrrg.wherecoffeeapplication.controller;

import com.github.nazzrrg.wherecoffeeapplication.model.User;
import com.github.nazzrrg.wherecoffeeapplication.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public void create(@RequestBody User user) {
        service.create(user);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public List<User> getUsersByRole(@RequestParam(value = "role", defaultValue = "ROLE_USER") String role) {
        /** добавить параметр username - для поиска и limit - количество совпадений */
        return service.getUsersByRole(role);
    }

    @GetMapping("/users/{id}")
    public User getById(@PathVariable long id) {
        return service.getById(id);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
