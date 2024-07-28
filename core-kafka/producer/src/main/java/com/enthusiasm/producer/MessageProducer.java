package com.enthusiasm.producer;

public interface MessageProducer {
    void send(String topic, String key, byte[] value);
}
