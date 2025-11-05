package com.dmitry.weathersdk.exception;

public class RateLimitException extends WeatherSdkException {
    public RateLimitException(String m) {
        super(m);
    }
}