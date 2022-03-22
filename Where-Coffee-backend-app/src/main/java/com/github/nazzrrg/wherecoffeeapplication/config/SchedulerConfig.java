package com.github.nazzrrg.wherecoffeeapplication.config;

import com.github.nazzrrg.wherecoffeeapplication.controller.CafeController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    @Value("${netcracker.app.results}")
    private Integer results;
    @Value("${netcracker.app.areas}")
    private String areas;
    @Value("${netcracker.app.updateURL}")
    private String urlPattern;
    @Value("${netcracker.app.mapAPI}")
    private String apikey;
    private final CafeController cafeController;

    public SchedulerConfig(CafeController cafeController) {
        this.cafeController = cafeController;
    }

    @Scheduled(cron = "0 0 3 * * *") // everyday in 3 a.m.
    private void updateCoffeeSpotsFromAPI() {
        cafeController.updateCafeterias(results, areas, urlPattern, apikey);
    }
}
