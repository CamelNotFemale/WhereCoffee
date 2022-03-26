package com.github.nazzrrg.wherecoffeeapplication.service;

import com.github.nazzrrg.wherecoffeeapplication.repo.UserRepository;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class UserService {

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

    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    public User getById(long id) {
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }
}
