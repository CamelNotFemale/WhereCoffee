package com.github.nazzrrg.wherecoffeeapplication.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "points")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** latitude */
    private Double lat;
    /** longitude */
    private Double lng;

    public Point() {
    }
    public Point(String location) {
        location = location.substring(1, location.length()-1);
        lat = Double.parseDouble(location.split(",")[1]);
        lng = Double.parseDouble(location.split(",")[0]);
    }
    public Point(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
