package com.enthusiasm.events;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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