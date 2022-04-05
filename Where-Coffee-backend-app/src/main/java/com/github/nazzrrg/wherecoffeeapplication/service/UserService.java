package com.github.nazzrrg.wherecoffeeapplication.service;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.model.ERole;
import com.github.nazzrrg.wherecoffeeapplication.repo.UserRepository;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Value("${netcracker.app.itemsOnPage}")
    private int itemsOnPage;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void create(@RequestBody User user) {
        if (userRepository.existsByName(user.getName())) {
            userRepository.save(user);
        }
        else new RuntimeException("Error: username is taken!");
    }

    public Page<User> getPage(Integer page, String name, String role) {
        Pageable pageable = PageRequest.of(page, itemsOnPage);
        return userRepository.findUserByNameAndRole(name, role, pageable);
    }

    public User getById(long id) {
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }
}
