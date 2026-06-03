package com.activitytracker.backend.controller;

import com.activitytracker.backend.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherControllerUnitTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @Test
    void testGetWeather_Success() {
        double lat = 37.42;
        double lon = -122.08;
        when(weatherService.getWeatherDescription(lat, lon)).thenReturn("Sunny");

        ResponseEntity<String> response = weatherController.getWeather(lat, lon);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sunny", response.getBody());
    }
}