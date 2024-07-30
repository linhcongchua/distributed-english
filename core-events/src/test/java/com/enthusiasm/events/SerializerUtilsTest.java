package com.enthusiasm.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SerializerUtilsTest {

    @Test
    void test1() throws IOException {
        byte[] values = SerializerUtils.serializeToJsonBytes(new TestObject("value"));
        JsonNode jsonNode = SerializerUtils.serializeToJsonNode(values);
        assertEquals("{\"name\":\"value\"}", jsonNode.toString());
    }

    static class TestObject {
        public TestObject() {
        }

        public TestObject(String name) {
            this.name = name;
        }

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}