package com.github.nazzrrg.wherecoffeeapplication.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "hours")
public class Hours {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 10)
    private String weekday;
    private LocalTime start_time;
    private LocalTime end_time;

    public Hours() {
    }

    public Hours(String weekday, LocalTime start_time, LocalTime end_time) {
        this.weekday = weekday;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    @Override
    public String toString() {
        return "Hours{" +
                "weekday='" + weekday + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                '}';
    }
}
