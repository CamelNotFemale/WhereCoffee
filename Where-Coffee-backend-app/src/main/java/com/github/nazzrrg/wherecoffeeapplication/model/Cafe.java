package com.github.nazzrrg.wherecoffeeapplication.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "cafeterias")
public class Cafe {
    @Id
    private Long id;
    private String name;
    private String description;
    private String location;
    private String address;
    private String url;
    private String phone;
    private Double rating;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "manager")
    private User manager;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cafe_id")
    private List<Hours> workingHours;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cafeteria_id")
    private List<Grade> grades = new ArrayList<>();

    public Cafe() {
    }

    public Cafe(Long id, String name, String description, String location, String address, String url, String phone) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.address = address;
        this.url = url;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Cafe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", address='" + address + '\'' +
                ", url='" + url + '\'' +
                ", phone='" + phone + '\'' +
                ", rating=" + rating +
                ", manager=" + manager +
                ", workingHours=" + workingHours +
                ", grades=" + grades +
                '}';
    }
}
