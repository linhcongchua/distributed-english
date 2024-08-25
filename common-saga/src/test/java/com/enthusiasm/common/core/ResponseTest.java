package com.enthusiasm.common.core;

import com.enthusiasm.common.jackson.DeserializerUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTest {

    @Test
    void test() {
        String jsonStr = "{\"status\": \"SUCCESS\"}";
        Response response = DeserializerUtils.deserialize(jsonStr.getBytes(StandardCharsets.UTF_8), Response.class);
        assertEquals(Response.Status.SUCCESS, response.status());
    }
}