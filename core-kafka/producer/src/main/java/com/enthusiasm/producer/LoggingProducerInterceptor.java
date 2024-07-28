package com.enthusiasm.producer;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

// need thread safety
public class LoggingProducerInterceptor implements ProducerInterceptor<String, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingProducerInterceptor.class);

    // todo: check open-telemetry for tracing
    @Override
    public ProducerRecord<String, byte[]> onSend(ProducerRecord<String, byte[]> record) {
        LOGGER.info("Sending message with key {} to topic {}", record.key(), record.topic());
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {
        // nothing to implement
    }
}
