package com.dmitry.weathersdk;

import com.dmitry.weathersdk.Mode;

import java.time.Duration;
import java.util.Objects;

public final class Options {

    private final Mode mode;
    private final Duration cacheTtl;
    private final int cacheMaxEntries;
    private final Duration httpTimeout;
    private final Duration pollingInterval;
    private final String units;
    private final String lang;

    private Options(
            Mode mode,
            Duration cacheTtl,
            int cacheMaxEntries,
            Duration httpTimeout,
            Duration pollingInterval,
            String units,
            String lang
    ) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.cacheTtl = Objects.requireNonNull(cacheTtl, "cacheTtl");
        this.cacheMaxEntries = cacheMaxEntries;
        this.httpTimeout = Objects.requireNonNull(httpTimeout, "httpTimeout");
        this.pollingInterval = Objects.requireNonNull(pollingInterval, "pollingInterval");
        this.units = units;
        this.lang = lang;
    }

    public static Options defaults() {
        return new Options(
                Mode.ON_DEMAND,
                Duration.ofMinutes(10),
                10,
                Duration.ofSeconds(10),
                Duration.ofMinutes(5),
                "metric",
                "en"
        );
    }

    public Mode mode() {
        return mode;
    }

    public Duration cacheTtl() {
        return cacheTtl;
    }

    public int cacheMaxEntries() {
        return cacheMaxEntries;
    }

    public Duration httpTimeout() {
        return httpTimeout;
    }

    public Duration pollingInterval() {
        return pollingInterval;
    }

    public String units() {
        return units;
    }

    public String lang() {
        return lang;
    }

    public Options withMode(Mode mode) {
        return new Options(mode, cacheTtl, cacheMaxEntries, httpTimeout, pollingInterval, units, lang);
    }

    public Options withCacheTtl(Duration ttl) {
        return new Options(mode, ttl, cacheMaxEntries, httpTimeout, pollingInterval, units, lang);
    }

    public Options withCacheMaxEntries(int n) {
        return new Options(mode, cacheTtl, n, httpTimeout, pollingInterval, units, lang);
    }

    public Options withHttpTimeout(Duration timeout) {
        return new Options(mode, cacheTtl, cacheMaxEntries, timeout, pollingInterval, units, lang);
    }

    public Options withPollingInterval(Duration interval) {
        return new Options(mode, cacheTtl, cacheMaxEntries, httpTimeout, interval, units, lang);
    }

    public Options withUnits(String units) {
        return new Options(mode, cacheTtl, cacheMaxEntries, httpTimeout, pollingInterval, units, lang);
    }

    public Options withLang(String lang) {
        return new Options(mode, cacheTtl, cacheMaxEntries, httpTimeout, pollingInterval, units, lang);
    }
}
