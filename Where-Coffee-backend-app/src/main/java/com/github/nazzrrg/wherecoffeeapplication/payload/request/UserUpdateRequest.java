package com.github.nazzrrg.wherecoffeeapplication.payload.request;

import lombok.Data;

import java.util.Date;
@Data
public class UserUpdateRequest {
    private String name;
    private String email;
    private String phone;
    private Date birthDay;
    private String oldPassword;
    private String newPassword;
}
