package com.github.nazzrrg.wherecoffeeapplication.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class GradeRequest {
    private String comment;
    private int grade;
    //private Long userId; - не нужен, т.к. берётся из текущей сессии пользователя
    private List<String> perks;
}
