package com.github.nazzrrg.wherecoffeeapplication.payload.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String email;
    //private Set<String> role;
    private String password;
}