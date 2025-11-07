package com.dmitry.weathersdk.cache;

import java.util.Set;

public interface Cache<K, V extends CacheEntry<?>> {
    V getIfFresh(K key);
    void put(K key, V value);
    default Set<K> keys() { return Set.of(); }
}
