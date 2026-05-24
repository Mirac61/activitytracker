package com.activitytracker.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    @JsonProperty("current")
    private Current current;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {

        @JsonProperty("condition")
        private Condition condition;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Condition {

        @JsonProperty("text")
        private String text;
    }
}
