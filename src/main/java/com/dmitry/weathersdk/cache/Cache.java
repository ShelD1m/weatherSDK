package com.dmitry.weathersdk.cache;

public interface Cache<K, V extends CacheEntry<?>> {
    V getIfFresh(K key);

    void put(K key, V value);
}