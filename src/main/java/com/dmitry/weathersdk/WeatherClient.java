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
            int sc = response.statusCode();
            String body = response.body();

            if (sc == 200) {
                return WeatherResponse.fromJson(body);
            }

            switch (sc) {
                case 401 ->
                        throw new com.dmitry.weathersdk.exception.AuthenticationException("Invalid/expired API key");
                case 404 -> throw new com.dmitry.weathersdk.exception.CityNotFoundException(city);
                case 429 -> throw new com.dmitry.weathersdk.exception.RateLimitException("Rate limit exceeded");
                default -> throw new com.dmitry.weathersdk.exception.NetworkException("HTTP " + sc + " — " + body);
            }

        } catch (IOException e) {
            throw new com.dmitry.weathersdk.exception.NetworkException("I/O error: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new com.dmitry.weathersdk.exception.NetworkException("Request interrupted", e);
        }
    }

}
