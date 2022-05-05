package com.github.nazzrrg.wherecoffeeapplication.utils;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.model.Hours;
import com.github.nazzrrg.wherecoffeeapplication.model.Point;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.CafeRequest;
import com.github.nazzrrg.wherecoffeeapplication.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DTOMapper {
    private final UserService userService;

    public DTOMapper(UserService userService) {
        this.userService = userService;
    }

    private List<Hours> updateWorkingHours(List<Hours> hoursOld, List<Hours> hoursNew) {
        for (Hours dayAPI: hoursNew) {
            boolean isPresent = false;
            for (Hours day: hoursOld) {
                if (day.getWeekday().equals(dayAPI.getWeekday())) {
                    day.setStart_time(dayAPI.getStart_time());
                    day.setEnd_time(dayAPI.getEnd_time());
                    isPresent = true;
                    break;
                }
            }
            if (!isPresent) hoursOld.add(dayAPI);
        }
        return hoursOld;
    }

    public Cafe updateCafeFromAPI(Cafe cafeToBeUpdated, Cafe cafeFromAPI) {
        cafeToBeUpdated.setName(cafeFromAPI.getName());
        cafeToBeUpdated.setDescription(cafeFromAPI.getDescription());
        cafeToBeUpdated.setUrl(cafeFromAPI.getUrl());
        cafeToBeUpdated.setPhone(cafeFromAPI.getPhone());
        cafeToBeUpdated.setAddress(cafeFromAPI.getAddress());

        List<Hours> workingHours = updateWorkingHours(cafeToBeUpdated.getWorkingHours(),
                cafeFromAPI.getWorkingHours());
        cafeToBeUpdated.setWorkingHours(workingHours);

        Point newPoint = cafeFromAPI.getLocation();
        cafeToBeUpdated.getLocation().setLng(newPoint.getLng());
        cafeToBeUpdated.getLocation().setLat(newPoint.getLat());

        return cafeToBeUpdated;
    }

    public Cafe fillCafeFromDTO(Cafe cafe, CafeRequest dto) {
        cafe.setName(dto.getName());
        cafe.setDescription(dto.getDescription());
        Point point = Optional.ofNullable(cafe.getLocation()).orElseGet(Point::new);
        String[] newLocationString = dto.getLocation().split(",");
        point.setLat(Double.parseDouble(newLocationString[0]));
        point.setLng(Double.parseDouble(newLocationString[1]));
        cafe.setLocation(point);
        cafe.setAddress(dto.getAddress());
        cafe.setPhone(dto.getPhone());
        cafe.setUrl(dto.getUrl());
        if (dto.getManagerId() != 0) {
            User manager = userService.getById(dto.getManagerId());
            cafe.setManager(manager);
        }
        List<Hours> workingHours = new ArrayList<>();
        dto.getWorkingHours().forEach(e -> {
            if (e.getStart_time() != null && e.getEnd_time() != null)
                workingHours.add(e);
        });
        cafe.setWorkingHours(workingHours);

        return cafe;
    }

    public Cafe toCafe(CafeRequest dto) {
        return fillCafeFromDTO(new Cafe(), dto);
    }
}
