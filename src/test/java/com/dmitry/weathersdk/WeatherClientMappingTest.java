package com.dmitry.weathersdk;

import com.dmitry.weathersdk.model.WeatherResponse;
import com.dmitry.weathersdk.util.Json;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherClientMappingTest {
    @Test
    void mapperLoadsMinimalJson() throws Exception {
        String json = """
      {"weather":[{"main":"Clouds","description":"few clouds"}],
       "main":{"temp":12.3,"feels_like":10.0},
       "visibility":8000,
       "wind":{"speed":5.4},
       "dt":1700000000,"sys":{"sunrise":1700000100,"sunset":1700040000},
       "timezone":10800, "name":"Moscow"}""";
        var ow = Json.mapper().readTree(json);
        assertEquals("Moscow", ow.get("name").asText());
    }
}
