package com.dmitry.weathersdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class OpenWeatherDtos {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Root {
        public java.util.List<Weather> weather;
        public Main main;
        public Wind wind;
        public int visibility;
        public long dt; // unix epoch
        public Sys sys;
        public int timezone; // seconds offset
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        public String main;
        public String description;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        public double temp;
        public double feels_like;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        public double speed;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        public long sunrise;
        public long sunset;
    }
}
