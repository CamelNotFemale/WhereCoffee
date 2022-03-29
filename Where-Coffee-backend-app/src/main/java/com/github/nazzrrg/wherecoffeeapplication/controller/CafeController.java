package com.github.nazzrrg.wherecoffeeapplication.controller;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.CafeRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.GradeRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.service.CafeService;
import com.github.nazzrrg.wherecoffeeapplication.service.UserService;
import com.github.nazzrrg.wherecoffeeapplication.service.YandexMapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public void create(@RequestBody CafeRequest cafe) {
        /** переделать с dto */
        System.out.println(cafe);
    }

    @GetMapping
    public List<Cafe> getCafePage(@RequestParam(value = "page") Integer page,
                                  @RequestParam(value = "location", defaultValue = "59.965361,30.311645") String location,
                                  @RequestParam(value = "dist", defaultValue = "1.0") Double dist) {
        /** переделать с dto */
        Page<Cafe> cafeterias = service.getPage(page, location, dist);
        return cafeterias.getContent();
    }

    @GetMapping("/{id}")
    public Cafe getCafe(@PathVariable long id) {
        return service.getById(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<MessageResponse> addRewiew(@PathVariable long id,
                                                     @RequestBody GradeRequest grade) {
        User user = userService.getById(grade.getUserId());
        return service.addRewiew(id, user, grade);
    }
    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateCafeterias(@RequestParam(value = "res",defaultValue = "1") Integer res,
                                                            @Value("${netcracker.app.areas}") String areas,
                                                            @Value("${netcracker.app.updateURL}") String urlPattern,
                                                            @Value("${netcracker.app.mapAPI}") String apikey) {
        return mapService.updateCafeterias(res, areas, urlPattern, apikey);
    }
}
