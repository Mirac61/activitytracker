package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class WeatherService {

    @Value("${weatherapi_key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    public String getWeatherDescription(double lat, double lon) {
        try {
        String url = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + lat + "," + lon;
        WeatherResponse response =  restTemplate.getForObject(url, WeatherResponse.class);
        return response.getCurrent().getCondition().getText();
        } catch (Exception e) {
            return "Weather data not available";
        }
    }
}