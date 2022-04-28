package com.github.nazzrrg.wherecoffeeapplication.payload.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}