package com.github.nazzrrg.wherecoffeeapplication.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String comment;
    @Min(value = 1) @Max(value = 5)
    private int grade;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "grade_perks",
            joinColumns = @JoinColumn(name = "grade_id"),
            inverseJoinColumns = @JoinColumn(name = "perk_id"))
    private Set<Perk> perks = new HashSet<>();;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
    private Date date;

    public Grade() {
    }

    public Grade(String comment, int grade, Set<Perk> perks, User user) {
        this.comment = comment;
        this.grade = grade;
        this.perks = perks;
        this.user = user;
        this.date = new Date();
    }
}
