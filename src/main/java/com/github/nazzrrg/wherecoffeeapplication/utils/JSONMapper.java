package com.github.nazzrrg.wherecoffeeapplication.utils;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.model.EDay;
import com.github.nazzrrg.wherecoffeeapplication.model.Hours;
import com.github.nazzrrg.wherecoffeeapplication.model.Point;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class JSONMapper {
    private static boolean checkEveryday(JSONObject availability) {
        return availability.get("Everyday") != null;
    }

    private static boolean checkTwentyFourHours(JSONObject availability) {
        return availability.get("TwentyFourHours") != null;
    }

    private static List<Hours> parseWorkingHours(JSONObject jo) {
        List<Hours> workingHours = new ArrayList<>();

        JSONArray availabilities = (JSONArray) jo.get("Availabilities");
        for (Object o : availabilities) {
            JSONObject availability = (JSONObject) o;
            String from;
            String to;

            if (checkTwentyFourHours(availability)) {
                from = "00:00";
                to = "23:59";
            } else {
                JSONArray intervals = (JSONArray) availability.get("Intervals");
                from = ((JSONObject) intervals.get(0)).get("from").toString();
                to = ((JSONObject) intervals.get(0)).get("to").toString();
            }
            if (checkEveryday(availability)) {
                for (EDay day : EDay.values()) {
                    workingHours.add(new Hours(day.name(),
                            LocalTime.parse(from),
                            LocalTime.parse(to)));
                }
            } else {
                for (String day : (Set<String>) availability.keySet()) {
                    try {
                        String checkDay = EDay.valueOf(day).name();
                        workingHours.add(new Hours(checkDay,
                                LocalTime.parse(from),
                                LocalTime.parse(to)));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }

        return workingHours;
    }

    private static String[] parseCoordinates(String location) {
        location = location.substring(1, location.length() - 1);
        String[] coord = location.split(",");
        String temp = coord[0];
        coord[0] = coord[1];
        coord[1] = temp;
        return coord;
    }

    public static Cafe toCafe(JSONObject jo) {
        Cafe cafe = new Cafe();

        JSONObject geometry = (JSONObject) jo.get("geometry");
        JSONArray coordinates = (JSONArray) geometry.get("coordinates");
        String location = coordinates.toString();
        cafe.setLocation(new Point(parseCoordinates(location)));
        JSONObject properties = (JSONObject) jo.get("properties");
        cafe.setName(properties.get("name").toString());
        cafe.setDescription(properties.get("description").toString());
        JSONObject companyMetaData = (JSONObject) properties.get("CompanyMetaData");
        cafe.setIdApi(Long.parseLong(companyMetaData.get("id").toString()));
        cafe.setAddress(companyMetaData.get("address").toString());
        if (companyMetaData.get("url") != null) cafe.setUrl(companyMetaData.get("url").toString());
        JSONArray phones = (JSONArray) companyMetaData.get("Phones");
        if (phones != null) {
            String phonesOnString = "";
            for (Object phone : phones) {
                phonesOnString += ((JSONObject) phone).get("formatted").toString() + ";";
            }
            cafe.setPhone(phonesOnString);
        }
        JSONObject workingHours = (JSONObject) companyMetaData.get("Hours");
        if (workingHours != null) cafe.setWorkingHours(parseWorkingHours(workingHours));

        return cafe;
    }
}
