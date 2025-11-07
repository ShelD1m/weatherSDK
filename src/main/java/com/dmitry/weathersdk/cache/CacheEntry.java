package com.dmitry.weathersdk.cache;

import java.time.Duration;
import java.time.Instant;

public final class CacheEntry<T> {
    private final T value;
    private final Instant storedAt;

    public CacheEntry(T value) {
        this.value = value;
        this.storedAt = Instant.now();
    }

    public T value() {
        return value;
    }

    public Instant storedAt() {
        return storedAt;
    }
    public boolean isExpired(Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) return false;
        return storedAt.plus(ttl).isBefore(Instant.now());
    }
}
