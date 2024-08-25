package com.enthusiasm.consumer;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoggingConsumerInterceptor implements ConsumerInterceptor<String, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingConsumerInterceptor.class);

    @Override
    public ConsumerRecords<String, byte[]> onConsume(ConsumerRecords<String, byte[]> records) {
        LOGGER.info("---> Received {} messages", records.count());
        return records;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        // nothing to do
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
