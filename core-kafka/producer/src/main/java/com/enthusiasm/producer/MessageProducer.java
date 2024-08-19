package com.enthusiasm.producer;

import org.apache.kafka.common.header.Header;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface MessageProducer {
    void send(String topic, String key, byte[] value);

    void send(String topic, String key, byte[] value, Map<String, String> headers);

    void send(String topic, String key, byte[] value, Supplier<List<Header>> headersSupplier);
}
