package com.github.nazzrrg.wherecoffeeapplication.config;

import com.github.nazzrrg.wherecoffeeapplication.controller.CafeController;
import com.github.nazzrrg.wherecoffeeapplication.repo.PromotionRepository;
import com.github.nazzrrg.wherecoffeeapplication.service.YandexMapService;
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
    private final YandexMapService mapService;
    private final PromotionRepository promotionRepository;

    public SchedulerConfig(YandexMapService mapService, PromotionRepository promotionRepository) {
        this.mapService = mapService;
        this.promotionRepository = promotionRepository;
    }

    @Scheduled(cron = "0 0 3 * * *") // everyday in 3 a.m.
    private void updateCoffeeSpotsFromAPI() {
        mapService.updateCafeterias(results, areas, urlPattern, apikey);
    }

    @Scheduled(cron = "0 0 2 * * *") // everyday in 2 a.m.
    private void deleteIrrelevantPromotions() {
        promotionRepository.deleteIrrelevantPromotions();
    }
}
