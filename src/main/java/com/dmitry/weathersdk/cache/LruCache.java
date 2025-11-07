package com.dmitry.weathersdk.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V extends CacheEntry<?>> implements Cache<K, V> {
    private final int maxEntries;
    private final Duration ttl;
    private final Map<K, V> map;

    public LruCache(int maxEntries, Duration ttl) {
        this.maxEntries = maxEntries;
        this.ttl = ttl;
        this.map = new LinkedHashMap<>(16, 0.75f, true);
    }

    @Override
    public synchronized V getIfFresh(K key) {
        V entry = map.get(key);
        if (entry == null) return null;
        if (entry.isExpired(ttl)) {
            map.remove(key);
            logCacheClear("Cache expired for key: " + key);
            return null;
        }
        return entry;
    }

    @Override
    public synchronized void put(K key, V value) {
        if (map.size() >= maxEntries) {
            K firstKey = map.keySet().iterator().next();
            map.remove(firstKey);
            logCacheClear("Cache full — removed oldest entry: " + firstKey);
        }
        map.put(key, value);
    }

    @Override
    public synchronized java.util.Set<K> keys() {
        return new java.util.LinkedHashSet<>(map.keySet());
    }

    private void logCacheClear(String message) {
        final String DARK_GRAY = "\u001B[90m";
        final String RESET = "\u001B[0m";
        final String TIME = Instant.now().toString();
        System.out.println(DARK_GRAY + "[" + TIME + "] " + message + RESET);
    }

    public synchronized void cleanup() {
        map.entrySet().removeIf(e -> e.getValue().isExpired(ttl));
        logCacheClear("Manual cache cleanup completed.");
    }
}
