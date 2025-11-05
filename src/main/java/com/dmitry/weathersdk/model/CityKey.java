package com.dmitry.weathersdk.model;

public final class CityKey {
    private CityKey() {
    }

    public static String normalize(String city) {
        if (city == null) throw new IllegalArgumentException("city is null");
        return city.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
