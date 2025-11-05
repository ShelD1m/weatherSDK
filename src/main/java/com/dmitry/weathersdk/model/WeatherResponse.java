package com.dmitry.weathersdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherResponse(
        String location,
        String condition,
        String description,
        double temperature,
        double feelsLike,
        int visibility,
        double windSpeed,
        Instant datetime,
        Instant sunrise,
        Instant sunset,
        int timezoneOffsetSeconds
) {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static WeatherResponse fromJson(String json) throws IOException {
        JsonNode root = MAPPER.readTree(json);

        String name = root.path("name").asText();
        int timezone = root.path("timezone").asInt();
        JsonNode weather = root.path("weather").get(0);
        String condition = weather.path("main").asText();
        String desc = weather.path("description").asText();

        JsonNode main = root.path("main");
        double temp = main.path("temp").asDouble();
        double feels = main.path("feels_like").asDouble();

        int vis = root.path("visibility").asInt();
        double wind = root.path("wind").path("speed").asDouble();

        Instant dt = Instant.ofEpochSecond(root.path("dt").asLong());
        JsonNode sys = root.path("sys");
        Instant sunrise = Instant.ofEpochSecond(sys.path("sunrise").asLong());
        Instant sunset = Instant.ofEpochSecond(sys.path("sunset").asLong());

        return new WeatherResponse(name, condition, desc, temp, feels, vis, wind, dt, sunrise, sunset, timezone);
    }
}
