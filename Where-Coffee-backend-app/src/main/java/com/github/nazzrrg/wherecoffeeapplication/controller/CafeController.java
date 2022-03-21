package com.github.nazzrrg.wherecoffeeapplication.controller;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.model.Grade;
import com.github.nazzrrg.wherecoffeeapplication.model.Hours;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.service.CafeService;
import com.github.nazzrrg.wherecoffeeapplication.service.UserService;
import com.github.nazzrrg.wherecoffeeapplication.utils.JSONMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    public CafeController(CafeService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }
    @PostMapping
    //@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public void create(@RequestBody(required = false) Cafe cafe) {
        // тестовая загрузка кафе, убрать потом
        userService.create(new User("name", "email", "password"));
        Cafe cafe1 = new Cafe(
                123l,
                "Test Cafe",
                "For test",
                "3.5;45.34",
                "Проспект Испытатателей",
                "url",
                "+791231234545"
        );
        User user = userService.getById(1);
        List<Grade> grades = Arrays.asList(new Grade("very cool", 5, user));
        cafe1.setGrades(grades);
        cafe1.setManager(user);
        List<Hours> hours = Arrays.asList(new Hours("MON", LocalTime.of(8,0), LocalTime.of(20,0)),
                new Hours("THU", LocalTime.of(9,0), LocalTime.of(20,0)));
        cafe1.setWorkingHours(hours);
        service.create(cafe1);
    }

    @GetMapping("/{id}")
    public Cafe getCafe(@PathVariable long id) {
        return service.getById(id);
    }
    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateCafeterias(@RequestParam("geo") String geo,
                                                            @RequestParam("res") Integer res,
                                                            @Value("${netcracker.app.mapAPI}") String apikey) throws Exception {
        String request = String.format(
                "https://search-maps.yandex.ru/v1/?text=кофе,Санкт-Петербург,%s&results=%d&type=biz&lang=ru_RU&apikey=%s",
                geo, res, apikey);
        final RestTemplate restTemplate = new RestTemplate();
        final String str = restTemplate.getForObject(request, String.class);
        JSONObject jo = (JSONObject) new JSONParser().parse(str);
        JSONArray cafeterias = (JSONArray) jo.get("features");
        int count = 0;
        for (int i =0; i< cafeterias.size(); i++) {
            JSONObject joCafe = (JSONObject) cafeterias.get(i);
            Cafe cafe = JSONMapper.toCafe(joCafe);
            if (service.create(cafe)) {
                count++;
            }
        }

        return ResponseEntity
                .created(URI.create("http://localhost/cafeterias/update?geo="+geo+"&res="+res))
                .body(new MessageResponse("Out of "+cafeterias.size()+" found successfully added "+count+" coffee shops"));
    }
}
