package com.enthusiasm.producer;

import java.util.Map;

public interface MessageProducer {
    void send(String topic, String key, byte[] value);

    void send(String topic, String key, byte[] value, Map<String, String> headers);
}
