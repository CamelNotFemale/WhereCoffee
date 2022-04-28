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
    public Point(String location[]) {
        lat = Double.parseDouble(location[0]);
        lng = Double.parseDouble(location[1]);
    }
    public Point(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return lat+","+lng;
    }
}
