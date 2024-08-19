package com.enthusiasm.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MessageProducerImpl implements MessageProducer, Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducerImpl.class);
    private final KafkaProducer<String, byte[]> delegateProducer;

    public MessageProducerImpl(ProducerProperties config) {
        delegateProducer = new KafkaProducer<>(config.getDefault());
    }

    @Override
    public void send(String topic, String key, byte[] value) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, value);
        delegateProducer.send(record);
        LOGGER.info("Sent message to topic {} with key {}", topic, key);
    }

    @Override
    public void send(String topic, String key, byte[] value, Map<String, String> headers) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, null, key, value, toList(headers));
        delegateProducer.send(record);
        LOGGER.info("Sent message to topic {} with key {}", topic, key);
    }

    private List<Header> toList(Map<String, String> headers) {
        List<Header> recordHeaders = new ArrayList<>();
        headers.forEach((headerKey, headerValue) -> {
            recordHeaders.add(new RecordHeader(headerKey, headerValue.getBytes(StandardCharsets.UTF_8)));
        });
        return recordHeaders;
    }


    @Override
    public void send(String topic, String key, byte[] value, Supplier<List<Header>> headersSupplier) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, null, key, value, headersSupplier.get());
        delegateProducer.send(record);
        LOGGER.info("Sent message to topic {} with key {}", topic, key);
    }


    @Override
    public void close() throws IOException {
        delegateProducer.close();
    }
}
