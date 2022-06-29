package com.github.nazzrrg.wherecoffeeapplication.payload.request;

import com.github.nazzrrg.wherecoffeeapplication.model.Hours;
import lombok.Data;

import java.util.List;

@Data
public class CafeRequest {
    private String name;
    private String description;
    private String location;
    private String address;
    private String url;
    private String phone;
    private long managerId; // id?
    private List<Hours> workingHours;

    @Override
    public String toString() {
        return "CafeRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", address='" + address + '\'' +
                ", url='" + url + '\'' +
                ", phone='" + phone + '\'' +
                ", manager='" + managerId + '\'' +
                ", workingHours=" + workingHours +
                '}';
    }
}
