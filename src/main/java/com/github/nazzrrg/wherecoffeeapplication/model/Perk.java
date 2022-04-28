package com.github.nazzrrg.wherecoffeeapplication.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "perks")
public class Perk {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private EPerk title;

    public Perk() {
    }

    public Perk(EPerk title) {
        this.title = title;
    }
}
