package com.github.nazzrrg.wherecoffeeapplication.payload.request;

import lombok.Data;

import java.util.Date;
@Data
public class UserUpdateRequest {
    private String firstName;
    private String surname;
    private String patronymic;
    private Date birthDay;
    private String email;
    private String phone;
    private String oldPassword;
    private String newPassword;
}
