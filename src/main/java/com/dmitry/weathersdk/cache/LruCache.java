package com.dmitry.weathersdk.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V extends CacheEntry<?>> implements Cache<K, V> {
    private final Duration ttl;
    private final Map<K, V> map;

    public LruCache(int maxEntries, Duration ttl) {
        this.ttl = ttl;
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxEntries;
            }
        };
    }

    @Override
    public synchronized V getIfFresh(K key) {
        V e = map.get(key);
        if (e == null) return null;
        if (Duration.between(e.createdAt(), Instant.now()).compareTo(ttl) > 0) {
            map.remove(key);
            return null;
        }
        return e;
    }

    @Override
    public synchronized void put(K key, V value) {
        map.put(key, value);
    }
}
