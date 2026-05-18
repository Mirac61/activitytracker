package com.activitytracker.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @Test
    void getWeatherDescription() {
        String result = weatherService.getWeatherDescription();
        System.out.println("Aktuelles Wetter: " + result);
        assertNotNull(result);
    }
}