package com.enthusiasm.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class DeserializerUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private DeserializerUtils() {
    }

    public static  <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}