package com.dmitry.weathersdk;

import com.dmitry.weathersdk.cache.CacheEntry;
import com.dmitry.weathersdk.cache.LruCache;
import com.dmitry.weathersdk.model.CityKey;
import com.dmitry.weathersdk.model.WeatherResponse;
import com.dmitry.weathersdk.polling.PollingScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Основной класс SDK для получения погоды.
 * Реализует два режима работы:
 * - ON_DEMAND: запрос данных по требованию с кэшированием на 10 минут
 * - POLLING: периодическое обновление кэша (Zero-Latency)
 * <p>
 * Кэш ограничен 10 городами и временем жизни 10 минут.
 */
public final class WeatherSdk implements AutoCloseable {

    private final String apiKey;
    private final Options options;
    private final WeatherClient client;

    private final LruCache<String, CacheEntry<WeatherResponse>> cache;
    private final AtomicReference<PollingScheduler> schedulerRef = new AtomicReference<>(null);

    public static WeatherSdk create(String apiKey) {
        return create(apiKey, Options.defaults());
    }

    public static WeatherSdk create(String apiKey, Options options) {
        return new WeatherSdk(apiKey, options);
    }

    public static WeatherSdk create(String apiKey, Mode mode, Options options) {
        Options finalOptions = (options != null ? options : Options.defaults()).withMode(mode);
        return new WeatherSdk(apiKey, finalOptions);
    }

    public WeatherSdk(String apiKey, Options options) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey must not be blank");
        }

        this.apiKey = apiKey;
        this.options = options != null ? options : Options.defaults();
        this.client = new WeatherClient(this.apiKey, this.options);

        this.cache = new LruCache<>(
                this.options.cacheMaxEntries(),
                this.options.cacheTtl()
        );

        if (this.options.mode() == Mode.POLLING) {
            startPolling();
        }
    }

    private void startPolling() {
        Duration period = options.pollingInterval();
        PollingScheduler scheduler = PollingScheduler.start(() -> {
            try {
                for (String key : cache.keys()) {
                    WeatherResponse fresh = client.fetchCurrent(key);
                    cache.put(key, new CacheEntry<>(fresh));
                }
            } catch (Exception ignored) {
            }
        }, period);

        schedulerRef.set(scheduler);
    }

    public WeatherResponse getCurrentWeather(String city) {
        Objects.requireNonNull(city, "city must not be null");
        String key = CityKey.normalize(city);
        var entry = cache.getIfFresh(key);
        if (entry != null) return entry.value();
        WeatherResponse response = client.fetchCurrent(key);
        cache.put(key, new CacheEntry<>(response));
        return response;
    }

    public String getCurrentWeatherJson(String city) {
        var w = getCurrentWeather(city);
        long dt = w.datetime() != null ? w.datetime().getEpochSecond() : Instant.now().getEpochSecond();
        long sunrise = w.sunrise() != null ? w.sunrise().getEpochSecond() : 0L;
        long sunset = w.sunset() != null ? w.sunset().getEpochSecond() : 0L;

        return """
                {
                  "weather": { "main": %s, "description": %s },
                  "temperature": { "temp": %s, "feels_like": %s },
                  "visibility": %s,
                  "wind": { "speed": %s },
                  "datetime": %d,
                  "sys": { "sunrise": %d, "sunset": %d },
                  "timezone": %d,
                  "name": %s
                }
                """.formatted(
                jsonq(w.condition()),
                jsonq(w.description()),
                jsonn(w.temperature()),
                jsonn(w.feelsLike()),
                jsonn(w.visibility()),
                jsonn(w.windSpeed()),
                dt, sunrise, sunset,
                w.timezoneOffsetSeconds(),
                jsonq(w.location())
        ).trim();
    }

    private static String jsonq(String s) {
        return s == null ? "null" : "\"" + s.replace("\"", "\\\"") + "\"";
    }

    private static String jsonn(Number n) {
        return n == null ? "null" : n.toString();
    }
    @Override
    public void close() {
        var scheduler = schedulerRef.getAndSet(null);
        if (scheduler != null) {
            try {
                scheduler.close();
            } catch (Exception ignored) {
            }
        }
    }
}
