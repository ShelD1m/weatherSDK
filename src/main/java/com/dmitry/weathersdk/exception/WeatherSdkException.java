package com.dmitry.weathersdk.exception;

public class WeatherSdkException extends RuntimeException {
    public WeatherSdkException(String m) {
        super(m);
    }

    public WeatherSdkException(String m, Throwable c) {
        super(m, c);
    }
}