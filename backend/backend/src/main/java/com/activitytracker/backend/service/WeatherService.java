package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weatherapi_key}")
    private String apiKey;

    @Value("${weatherapi_city}")
    private String city;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getWeatherDescription() {
        String url = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + city;
        WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
        return response.getCurrent().getCondition().getText();
    }
}