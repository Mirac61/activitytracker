package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.WeatherConditionDto;
import com.activitytracker.backend.dto.WeatherCurrentDto;
import com.activitytracker.backend.dto.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "apiKey", "test_key");
    }

    @Test
    void getWeatherDescription_ShouldReturnConditionText() {
        double lat = 46.7;
        double lon = 9.1;
        String expectedUrl = "https://api.weatherapi.com/v1/current.json?key=test_key&q=46.7,9.1";
        String expectedCondition = "Sunny";

        WeatherResponse mockResponse = org.mockito.Mockito.mock(WeatherResponse.class);
        WeatherCurrentDto mockWeatherCurrentDto = org.mockito.Mockito.mock(WeatherCurrentDto.class);
        WeatherConditionDto mockWeatherConditionDto = org.mockito.Mockito.mock(WeatherConditionDto.class);

        when(mockResponse.getWeatherCurrentDto()).thenReturn(mockWeatherCurrentDto);
        when(mockWeatherCurrentDto.getWeatherConditionDto()).thenReturn(mockWeatherConditionDto);
        when(mockWeatherConditionDto.getText()).thenReturn(expectedCondition);

        when(restTemplate.getForObject(expectedUrl, WeatherResponse.class))
                .thenReturn(mockResponse);

        String actualCondition = weatherService.getWeatherDescription(lat,lon);

        assertEquals(expectedCondition, actualCondition);
    }
}