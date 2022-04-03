package com.github.nazzrrg.wherecoffeeapplication.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@IdClass(CafePerkId.class)
@Table(name = "cafeterias_perks")
public class CafePerk {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafeteria_id")
    private Cafe cafe;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perk_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Perk perk;
    private int count;

    public CafePerk() {
    }

    @JsonBackReference
    public Cafe getCafe() {
        return cafe;
    }
}
