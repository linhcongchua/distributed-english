package com.enthusiasm.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.function.Consumer;

public interface MessageHandler extends Consumer<ConsumerRecord<String, byte[]>> {
}
