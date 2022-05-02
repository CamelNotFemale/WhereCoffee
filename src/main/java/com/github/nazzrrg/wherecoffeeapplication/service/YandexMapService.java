package com.github.nazzrrg.wherecoffeeapplication.service;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.utils.JSONMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class YandexMapService {
    private final CafeService cafeService;

    public YandexMapService(CafeService cafeService) {
        this.cafeService = cafeService;
    }

    public JSONArray getJSONFromAPI(String request) {
        final RestTemplate restTemplate = new RestTemplate();
        final String str = restTemplate.getForObject(request, String.class);
        JSONObject jo = null;
        try {
            jo = (JSONObject) new JSONParser().parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray cafeterias = (JSONArray) jo.get("features");
        return cafeterias;
    }

    public int uploadFromJSON(JSONArray cafeterias) {
        int count = 0;
        for (int i = 0; i < cafeterias.size(); i++) {
            JSONObject joCafe = (JSONObject) cafeterias.get(i);
            Cafe cafe = JSONMapper.toCafe(joCafe);
            cafe.setConfirmed(true);
            if (cafeService.create(cafe)) {
                count++;
            }
        }
        return count;
    }

    public ResponseEntity<MessageResponse> updateCafeterias(Integer res, String areas,
                                                            String urlPattern, String apikey) {
        int count = 0, size = 0;
        for (String area : areas.split(";")) {
            String request = String.format(urlPattern, area, res, apikey);
            JSONArray cafeterias = getJSONFromAPI(request);
            size += cafeterias.size();
            count += uploadFromJSON(cafeterias);
        }

        return ResponseEntity
                .created(URI.create("http://localhost/cafeterias/update?res=" + res))
                .body(new MessageResponse("Out of " + size + " found successfully added " + count + " coffee shops"));
    }
}
