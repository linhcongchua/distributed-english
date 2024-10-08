package com.enthusiasm.common.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SerializerUtils {
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(new Jdk8Module())
            .build();

    private SerializerUtils() {
    }

    public static byte[] serializeToJsonBytes(final Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String serializeToJsonStr(final Object object) {
        return new String(serializeToJsonBytes(object), StandardCharsets.UTF_8);
    }

    public static <T> T deserializeFromJsonBytes(final byte[] jsonBytes, final Class<T> valueType) {
        try {
            return OBJECT_MAPPER.readValue(jsonBytes, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static JsonNode serializeToJsonNode(final byte[] jsonBytes) {
        try {
            return OBJECT_MAPPER.readTree(jsonBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode serializeToJsonNode(final Object object) {
        try {
            byte[] bytes = serializeToJsonBytes(object);
            return OBJECT_MAPPER.readTree(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
