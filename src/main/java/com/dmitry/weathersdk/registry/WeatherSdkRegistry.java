package com.dmitry.weathersdk.registry;


import com.dmitry.weathersdk.Mode;
import com.dmitry.weathersdk.Options;
import com.dmitry.weathersdk.WeatherSdk;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;


public final class WeatherSdkRegistry {
    private static final ConcurrentHashMap<String, WeakReference<WeatherSdk>> REG = new ConcurrentHashMap<>();

    private WeatherSdkRegistry() {
    }


    public static synchronized WeatherSdk getOrCreate(String apiKey, Mode mode, Options options) {
        WeakReference<WeatherSdk> ref = REG.get(apiKey);
        WeatherSdk existing = ref != null ? ref.get() : null;
        if (existing != null) return existing;
        WeatherSdk created = WeatherSdk.create(apiKey, mode, options);
        REG.put(apiKey, new WeakReference<>(created));
        return created;
    }


    public static synchronized void delete(String apiKey) {
        REG.remove(apiKey);
    }
}