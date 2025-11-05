package com.dmitry.weathersdk;

import com.dmitry.weathersdk.model.WeatherResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class WeatherClient {

    private final String apiKey;
    private final Options options;
    private final HttpClient httpClient;

    public WeatherClient(String apiKey, Options options) {
        this.apiKey = apiKey;
        this.options = options;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(options.httpTimeout())
                .build();
    }

    public WeatherResponse fetchCurrent(String city) {
        try {
            String units = options.units() != null ? options.units() : "metric";
            String lang = options.lang() != null ? options.lang() : "en";

            String url = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=%s&lang=%s",
                    city, apiKey, units, lang
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(options.httpTimeout())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("HTTP " + response.statusCode() + " — " + response.body());
            }

            return WeatherResponse.fromJson(response.body());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка при получении погоды: " + e.getMessage(), e);
        }
    }
}
