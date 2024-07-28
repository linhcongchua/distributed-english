package com.enthusiasm.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;

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
    public void close() throws IOException {
        delegateProducer.close();
    }
}
