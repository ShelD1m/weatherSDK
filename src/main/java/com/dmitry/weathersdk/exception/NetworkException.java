package com.dmitry.weathersdk.exception;

public class NetworkException extends WeatherSdkException {
    public NetworkException(String m) {
        super(m);
    }

    public NetworkException(String m, Throwable c) {
        super(m, c);
    }
}
