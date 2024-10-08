package com.enthusiasm.common.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class DeserializerUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private DeserializerUtils() {
    }

    public static  <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static  <T> T deserialize(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static  <T> T deserialize(byte[] bytes, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(bytes, typeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
