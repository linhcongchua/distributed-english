package com.enthusiasm.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessageConsumerSingleThreadImpl implements MessageConsumer, Runnable, MessageSubscription {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerSingleThreadImpl.class);

    private final KafkaConsumer<String, byte[]> delegateConsumer;

    private final MessageHandler handler;

    private final String topic;

    private volatile boolean stopped = false;

    Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();

    public MessageConsumerSingleThreadImpl(KafkaConsumer<String, byte[]> delegateConsumer, MessageHandler handler, String topic) {
        this.delegateConsumer = delegateConsumer;
        this.handler = handler;
        this.topic = topic;
    }

    @Override
    public MessageSubscription subscribe(String topic) {
        new Thread(this).start();
        return this;
    }

    @Override
    public void run() {
        delegateConsumer.subscribe(Collections.singleton(topic));

        try {
            while (!stopped) {
                var records = delegateConsumer.poll(Duration.of(100, ChronoUnit.MILLIS));
                handleFetchedRecords(records);
                delegateConsumer.commitAsync(offsetsToCommit, (offsets, exception) -> {
                    if (exception != null) {
                        LOGGER.error("Commit failed for offsets {}", offsets, exception);
                    }
                });
            }
        } finally {
            delegateConsumer.close();
            LOGGER.info("Closed consumer");
        }
    }

    private void handleFetchedRecords(ConsumerRecords<String,byte[]> records) {
        for (var record : records) {
            handler.accept(record);
            offsetsToCommit.put(new TopicPartition(record.topic(), record.partition()),
                    new OffsetAndMetadata(record.offset() + 1));
        }
    }

    @Override
    public void unsubscribe() {
        stopped = true;
        delegateConsumer.wakeup();
    }
}
