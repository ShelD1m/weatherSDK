package com.dmitry.weathersdk.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public final class Json {
    private static final ObjectMapper M = new ObjectMapper().registerModule(new JavaTimeModule());

    private Json() {
    }

    public static ObjectMapper mapper() {
        return M;
    }
}
