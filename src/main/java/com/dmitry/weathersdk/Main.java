package com.dmitry.weathersdk;


import io.github.cdimascio.dotenv.Dotenv;

import java.util.Scanner;

public final class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String apiKey = firstNonBlank(
                System.getenv("OWM_API_KEY"),
                dotenv.get("OWM_API_KEY")
        );
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Укажи OWM_API_KEY в .env или переменных окружения.");
            System.exit(1);
        }

        var sdk = WeatherSdk.create(apiKey, Options.defaults().withMode(Mode.ON_DEMAND));

        System.out.println("Введи город (например: London или Zocca,IT). Пустая строка — выход.");
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String city = sc.nextLine().trim();
                if (city.isEmpty()) break;

                try {
                    var w = sdk.getCurrentWeather(city);
                    System.out.printf(
                            "%s: %s, %s | %.1f°C (ощущается %.1f) | ветер %.1f м/с | видимость %d м%n",
                            w.location(),
                            w.condition(), w.description(),
                            w.temperature(), w.feelsLike(),
                            w.windSpeed(), w.visibility()
                    );
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
        System.out.println("Готово.");
    }

    private static String firstNonBlank(String... xs) {
        if (xs == null) return null;
        for (String x : xs) if (x != null && !x.isBlank()) return x;
        return null;
    }
}
