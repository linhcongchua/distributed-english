package com.enthusiasm.payment.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonMapperUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static  <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
