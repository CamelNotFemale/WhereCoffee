package com.github.nazzrrg.wherecoffeeapplication.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.nazzrrg.wherecoffeeapplication.utils.CustomCafeSerializer;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Date fromDate;
    private Date toDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "cafeterias_promotions",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "cafeteria_id")
    )
    @JsonSerialize(using = CustomCafeSerializer.class)
    private List<Cafe> cafes = new ArrayList<>();

    public Promotion() {
    }

    public Promotion(String title, String description, Date from, Date to, User user) {
        this.title = title;
        this.description = description;
        this.fromDate = from;
        this.toDate = to;
        this.user = user;
    }
}
