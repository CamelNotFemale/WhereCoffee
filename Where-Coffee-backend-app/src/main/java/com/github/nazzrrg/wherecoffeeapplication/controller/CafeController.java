package com.github.nazzrrg.wherecoffeeapplication.controller;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.CafeRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.GradeRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.service.CafeService;
import com.github.nazzrrg.wherecoffeeapplication.service.UserService;
import com.github.nazzrrg.wherecoffeeapplication.service.YandexMapService;
import com.github.nazzrrg.wherecoffeeapplication.utils.DTOMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public void create(@RequestBody CafeRequest cafe) {
        service.create(cafe);
    }

    @GetMapping
    public List<Cafe> getCafePage(@RequestParam(value = "page") Integer page,
                                  @RequestParam(value = "location", defaultValue = "59.965361,30.311645") String location,
                                  @RequestParam(value = "dist", defaultValue = "1.0") Double dist,
                                  @RequestParam(value = "confirmed", defaultValue = "true") boolean confirmed) {
        /** переделать с dto */
        Page<Cafe> cafeterias = service.getPage(page, location, dist, confirmed);
        return cafeterias.getContent();
    }

    @GetMapping("/pages-count")
    public int getCountPage(@RequestParam(value = "location", defaultValue = "59.965361,30.311645") String location,
                            @RequestParam(value = "dist", defaultValue = "1.0") Double dist,
                            @RequestParam(value = "confirmed", defaultValue = "true") boolean confirmed) {
        return service.getPageCount(location, dist, confirmed);
    }

    @GetMapping("/{id}")
    public Cafe getCafe(@PathVariable long id) {
        /** переделать с dto */
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCafe(@PathVariable long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCafe(@PathVariable long id,
                           @RequestBody CafeRequest cafe) {
        service.update(id, cafe);
    }

    @PostMapping("/{id}/confirm")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void confirmCafe(@PathVariable long id) {
        service.confirm(id);
    }

    @PostMapping("/{id}/rewiew")
    public ResponseEntity<MessageResponse> addRewiew(@PathVariable long id,
                                                     @RequestBody GradeRequest grade) {
        User user = userService.getById(grade.getUserId());
        return service.addRewiew(id, user, grade);
    }
    @PatchMapping("/{id}/rewiew/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> updateRewiew(@PathVariable long id,
                             @PathVariable long userId,
                             @RequestBody GradeRequest grade) {
        return service.updateRewiew(id, userId, grade);
    }
    @DeleteMapping("/{id}/rewiew/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteRewiew(@PathVariable long id,
                                                        @PathVariable long userId) {
        return service.deleteRewiew(id, userId);
    }

    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateCafeterias(@RequestParam(value = "res",defaultValue = "1") Integer res,
                                                            @Value("${netcracker.app.areas}") String areas,
                                                            @Value("${netcracker.app.updateURL}") String urlPattern,
                                                            @Value("${netcracker.app.mapAPI}") String apikey) {
        return mapService.updateCafeterias(res, areas, urlPattern, apikey);
    }
}
