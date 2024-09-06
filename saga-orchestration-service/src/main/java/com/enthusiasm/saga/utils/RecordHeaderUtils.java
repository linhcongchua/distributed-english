package com.enthusiasm.saga.utils;

import com.enthusiasm.common.jackson.DeserializerUtils;
import com.enthusiasm.common.jackson.SerializerUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.util.Map;

public class RecordHeaderUtils {
    private RecordHeaderUtils() {
    }

    public static <T> T getHeader(ConsumerRecord<String, byte[]> record, String key, Class<T> clazz) {
        Headers headers = record.headers();
        Header header = headers.lastHeader(key);
        if (header == null) {
            return null; // todo: null or exception
        }
        byte[] valueHeader = header.value();
        return DeserializerUtils.deserialize(valueHeader, clazz);
    }

    public static <T> T getHeader(ConsumerRecord<String, byte[]> record, String extraHeaderKey, String key, Class<T> clazz) {
        Headers headers = record.headers();
        Header header = headers.lastHeader(extraHeaderKey);
        if (header == null) {
            return null; // todo: null or exception
        }
        byte[] extraValueHeader = header.value();
        var typeRef = new TypeReference<Map<String, Object>>() {};
        Map<String, Object> extraHeader = DeserializerUtils.deserialize(extraValueHeader, typeRef);
        Object keyValue = extraHeader.get(key);
        byte[] keyByteValue = SerializerUtils.serializeToJsonBytes(keyValue);
        return DeserializerUtils.deserialize(keyByteValue, clazz);
    }
}
