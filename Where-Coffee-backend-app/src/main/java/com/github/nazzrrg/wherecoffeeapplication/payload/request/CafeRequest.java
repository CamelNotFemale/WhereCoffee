package com.github.nazzrrg.wherecoffeeapplication.payload.request;

import com.github.nazzrrg.wherecoffeeapplication.model.Hours;
import lombok.Data;

import java.util.List;

@Data
public class CafeRequest {
    private Long id; // убрать
    private String name;
    private String desc; // переименовать
    private String location;
    private String address;
    private String url;
    private String phone;
    private Double rating; // убрать
    private String manager; // ???
    private List<Hours> workingHours;
    private List<Integer> grades; // убрать

    @Override
    public String toString() {
        return "CafeRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", location='" + location + '\'' +
                ", address='" + address + '\'' +
                ", url='" + url + '\'' +
                ", phone='" + phone + '\'' +
                ", rating=" + rating +
                ", manager='" + manager + '\'' +
                ", workingHours=" + workingHours +
                ", grades=" + grades +
                '}';
    }
}
