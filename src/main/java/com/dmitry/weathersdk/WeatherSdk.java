package com.dmitry.weathersdk;

import com.dmitry.weathersdk.model.WeatherResponse;

import java.time.Instant;
import java.util.Objects;

public final class WeatherSdk {

    private final String apiKey;
    private final Options options;
    private final WeatherClient client;

    public static WeatherSdk create(String apiKey) {
        return create(apiKey, Options.defaults());
    }

    public static WeatherSdk create(String apiKey, Options options) {
        return new WeatherSdk(apiKey, options);
    }

    public WeatherSdk(String apiKey, Options options) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey must not be blank");
        }
        this.apiKey = apiKey;
        this.options = options != null ? options : Options.defaults();
        this.client = new WeatherClient(this.apiKey, this.options);

        if (this.options.mode() == Mode.POLLING) {
        }
    }

    public WeatherResponse getCurrentWeather(String city) {
        Objects.requireNonNull(city, "city");
        return client.fetchCurrent(city);
    }

    public String getCurrentWeatherJson(String city) {
        var w = getCurrentWeather(city);
        long dt = w.datetime() != null ? w.datetime().getEpochSecond() : Instant.now().getEpochSecond();
        long sunrise = w.sunrise() != null ? w.sunrise().getEpochSecond() : 0L;
        long sunset = w.sunset() != null ? w.sunset().getEpochSecond() : 0L;

        return """
                {
                  "weather": { "main": %s, "description": %s },
                  "temperature": { "temp": %s, "feels_like": %s },
                  "visibility": %s,
                  "wind": { "speed": %s },
                  "datetime": %d,
                  "sys": { "sunrise": %d, "sunset": %d },
                  "timezone": %d,
                  "name": %s
                }
                """.formatted(
                jsonq(w.condition()),
                jsonq(w.description()),
                jsonn(w.temperature()),
                jsonn(w.feelsLike()),
                jsonn(w.visibility()),
                jsonn(w.windSpeed()),
                dt, sunrise, sunset,
                w.timezoneOffsetSeconds(),
                jsonq(w.location())
        ).trim();
    }

    private static String jsonq(String s) {
        if (s == null) return "null";
        String e = s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return "\"" + e + "\"";
    }

    private static String jsonn(Number n) {
        return n == null ? "null" : n.toString();
    }

    public static WeatherSdk create(String apiKey, Mode mode, Options options) {
        Options finalOptions = (options != null ? options : Options.defaults()).withMode(mode);
        return new WeatherSdk(apiKey, finalOptions);
    }
}
