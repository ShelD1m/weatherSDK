package com.dmitry.weathersdk.cache;

import java.time.Instant;

public record CacheEntry<T>(T value, Instant createdAt) {
    public CacheEntry(T value) {
        this(value, Instant.now());
    }
}
