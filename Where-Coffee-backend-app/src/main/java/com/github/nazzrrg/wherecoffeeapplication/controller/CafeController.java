package com.github.nazzrrg.wherecoffeeapplication.controller;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.model.Grade;
import com.github.nazzrrg.wherecoffeeapplication.model.Hours;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.service.CafeService;
import com.github.nazzrrg.wherecoffeeapplication.service.UserService;
import com.github.nazzrrg.wherecoffeeapplication.service.YandexMapService;
import com.github.nazzrrg.wherecoffeeapplication.utils.JSONMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/cafeterias")
public class CafeController {
    private final CafeService service;
    private final UserService userService;
    private final YandexMapService mapService;
    public CafeController(CafeService service, UserService userService, YandexMapService mapService) {
        this.service = service;
        this.userService = userService;
        this.mapService = mapService;
    }
    @PostMapping
    //@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public void create(@RequestBody(required = false) Cafe cafe) {
        // переделать с dto
    }

    @GetMapping("/{id}")
    public Cafe getCafe(@PathVariable long id) {
        return service.getById(id);
    }

    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateCafeterias(@RequestParam(value = "res",defaultValue = "1") Integer res,
                                                            @Value("${netcracker.app.areas}") String areas,
                                                            @Value("${netcracker.app.updateURL}") String urlPattern,
                                                            @Value("${netcracker.app.mapAPI}") String apikey) {
        return mapService.updateCafeterias(res, areas, urlPattern, apikey);
    }
}
