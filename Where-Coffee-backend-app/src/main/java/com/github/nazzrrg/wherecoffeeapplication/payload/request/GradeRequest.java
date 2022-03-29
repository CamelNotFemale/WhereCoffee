package com.github.nazzrrg.wherecoffeeapplication.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class GradeRequest {
    private String comment;
    private int grade;
    private Long userId;
    private List<String> perks;
}
