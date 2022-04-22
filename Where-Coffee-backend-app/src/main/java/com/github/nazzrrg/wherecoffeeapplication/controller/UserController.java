package com.github.nazzrrg.wherecoffeeapplication.controller;

import com.github.nazzrrg.wherecoffeeapplication.model.User;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.UserUpdateRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.security.jwt.JwtUtils;
import com.github.nazzrrg.wherecoffeeapplication.security.services.UserDetailsImpl;
import com.github.nazzrrg.wherecoffeeapplication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
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
    public List<User> getUsers(@RequestParam(value = "page") Integer page,
                                 @RequestParam(value = "username", defaultValue = "") String username,
                                 @RequestParam(value = "role", defaultValue = "ROLE_USER") String role) {
        Page<User> users = service.getPage(page, username, role);
        return users.getContent();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("isAuthenticated()")
    public User getById(@PathVariable long id) {
        return service.getById(id);
    }

    @PatchMapping("/users/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public User update(@PathVariable long id,
                       @RequestBody UserUpdateRequest user) {
        return service.update(id, user);
    }
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
