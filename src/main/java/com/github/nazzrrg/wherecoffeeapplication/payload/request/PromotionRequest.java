package com.github.nazzrrg.wherecoffeeapplication.payload.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PromotionRequest {
    private String title;
    private String description;
    private Date from;
    private Date to;
    private List<Long> cafeteriaIds;
}
