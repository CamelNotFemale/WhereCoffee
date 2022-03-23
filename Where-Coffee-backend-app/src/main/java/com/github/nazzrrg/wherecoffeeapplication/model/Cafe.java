package com.github.nazzrrg.wherecoffeeapplication.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "cafeterias")
public class Cafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name="id_api", unique = true)
    private Long idApi;
    private String name;
    private String description;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "point_id")
    private Point location;
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
    @JoinColumn(name="confirmed", columnDefinition = "boolean default false")
    private boolean confirmed;

    public Cafe() {
        this.confirmed = false;
    }

    public Cafe(Long idApi, String name, String description, Point location, String address, String url, String phone) {
        this.idApi = idApi;
        this.name = name;
        this.description = description;
        this.location = location;
        this.address = address;
        this.url = url;
        this.phone = phone;
        this.confirmed = false;
    }

    @Override
    public String toString() {
        return "Cafe{" +
                "id=" + id +
                ", idApi=" + idApi +
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
                ", confirmed=" + confirmed +
                '}';
    }
}
