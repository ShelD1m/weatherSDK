package com.dmitry.weathersdk;

import com.dmitry.weathersdk.cache.CacheEntry;
import com.dmitry.weathersdk.cache.LruCache;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CacheTest {
    @Test
    void expiresAfterTtl() throws InterruptedException {
        var cache = new LruCache<String, CacheEntry<String>>(10, Duration.ofMillis(20));
        cache.put("k", new CacheEntry<>("v"));
        Thread.sleep(30);
        assertNull(cache.getIfFresh("k"));
    }

    @Test
    void evictsLruOnOverflow() {
        var cache = new LruCache<String, CacheEntry<String>>(1, Duration.ofMinutes(10));
        cache.put("a", new CacheEntry<>("1"));
        cache.put("b", new CacheEntry<>("2"));
        assertNull(cache.getIfFresh("a"));
        assertNotNull(cache.getIfFresh("b"));
    }
}
