package com.activitytracker.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherCurrentDto {

    @JsonProperty("condition")
    private WeatherConditionDto weatherConditionDto;
}