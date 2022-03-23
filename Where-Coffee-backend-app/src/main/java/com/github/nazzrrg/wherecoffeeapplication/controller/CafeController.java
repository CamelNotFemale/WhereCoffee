package com.github.nazzrrg.wherecoffeeapplication.controller;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
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
    public void create(@RequestBody(required = false) Cafe cafe) {
        /** переделать с dto */
    }

    @GetMapping
    public List<Cafe> getCafePage(@RequestParam(value = "page") Integer page,
                                  @RequestParam(value = "location", defaultValue = "59.965361,30.311645") String location) {
        /** переделать с dto */
        Page<Cafe> cafeterias = service.getPage(page);
        double lon, lat, lon2, lat2;
        // точка центра поиска
        lon = Double.parseDouble(location.split(",")[0]);
        lat = Double.parseDouble(location.split(",")[1]);
        for (Cafe cafe: cafeterias.getContent()) {
            // координата заведения
            lon2 = Double.parseDouble(cafe.getLocation().split(",")[1]);
            lat2 = Double.parseDouble(cafe.getLocation().split(",")[0]);
            // расстояние от точки поиска до заведения в километрах
            double result = 111.2 * Math.sqrt( (lon - lon2)*(lon - lon2) + (lat - lat2)*Math.cos(Math.PI*lon/180)*(lat - lat2)*Math.cos(Math.PI*lon/180));
            // все кофейни в радиусе 1км
            if (result <= 1) {
                System.out.println(cafe);
            }
        }
        return cafeterias.getContent();
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
