package com.enthusiasm.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;

public class JsonMapperUtils {
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(new Jdk8Module())
            .build();

    private JsonMapperUtils() {
    }

    public static Object loadObject(Map<String, Object> dataMap, String className) {
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsBytes(dataMap), clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
