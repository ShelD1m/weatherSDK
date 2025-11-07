package com.dmitry.weathersdk.polling;

import java.io.Closeable;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PollingScheduler implements Closeable {
    private final ScheduledExecutorService exec;

    private PollingScheduler(Runnable task, Duration period) {
        this.exec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "weather-polling");
            t.setDaemon(true);
            return t;
        });
        this.exec.scheduleAtFixedRate(Objects.requireNonNull(task), 0,
                period.toSeconds(), TimeUnit.SECONDS);
    }
    public static PollingScheduler start(Runnable task, Duration period) {
        return new PollingScheduler(task, period);
    }

    public static PollingScheduler noop() {
        return new PollingScheduler(() -> {}, Duration.ofDays(3650)) {
            @Override public void close() {}
        };
    }

    @Override public void close() {
        if (exec != null) exec.shutdownNow();
    }
}
