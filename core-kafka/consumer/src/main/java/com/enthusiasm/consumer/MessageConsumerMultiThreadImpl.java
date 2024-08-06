package com.enthusiasm.consumer;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class MessageConsumerMultiThreadImpl implements MessageConsumer, Runnable, MessageSubscription, ConsumerRebalanceListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerMultiThreadImpl.class);

    private final KafkaConsumer<String, byte[]> delegateConsumer;
    private final ExecutorService executorService;
    private final MessageHandler handler;

    private final String topic;
    private final Map<TopicPartition, Task> activeTasks = new HashMap<>();
    private final Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
    private volatile boolean stopped = false;
    private long lastCommitTime = System.currentTimeMillis();


    public MessageConsumerMultiThreadImpl(ExecutorService executorService, ConsumerProperties config, MessageHandler handler, String topic) {
        delegateConsumer = new KafkaConsumer<>(config.getDefault());
        this.executorService = executorService;
        this.handler = handler;
        this.topic = topic;
    }

    @Override
    public MessageSubscription subscribe() {
        new Thread(this).start();
        return this;
    }

    @Override
    public void run() {
        delegateConsumer.subscribe(Collections.singleton(topic));

        try {
            while (!stopped) {
                final var records = delegateConsumer.poll(Duration.of(100, ChronoUnit.MILLIS));
                handleFetchedRecords(records);
                checkActiveTasks();
                commitOffsets();
            }
        } catch (WakeupException we) {
            if (!stopped) {
                throw we;
            }
        } finally {
            delegateConsumer.close();
        }
    }

    private void handleFetchedRecords(ConsumerRecords<String,byte[]> records) {
        if (records == null || records.isEmpty()) {
            return;
        }

        List<TopicPartition> partitionsToPause = new ArrayList<>();

        records.partitions().forEach(partition -> {
            final var partitionRecords = records.records(partition);
            Task task = new Task(partitionRecords, handler);
            partitionsToPause.add(partition);
            executorService.submit(task);
            activeTasks.put(partition, task);
        });

        delegateConsumer.pause(partitionsToPause); // todo: using rate-limit to increase throughput
    }

    private void checkActiveTasks() {
        List<TopicPartition> finishedTasksPartitions = new ArrayList<>();
        activeTasks.forEach((partition, task) -> {
            if (task.isFinished()) {
                finishedTasksPartitions.add(partition);
            }
            final long offset = task.getCurrentOffset();
            if (offset > 0) {
                offsetsToCommit.put(partition, new OffsetAndMetadata(offset));
            }
        });

        finishedTasksPartitions.forEach(activeTasks::remove);
        delegateConsumer.resume(finishedTasksPartitions);
    }

    private void commitOffsets() {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastCommitTime > 5000) {
                if (!offsetsToCommit.isEmpty()) {
                    delegateConsumer.commitSync(offsetsToCommit);
                    offsetsToCommit.clear();
                }
                lastCommitTime = currentTimeMillis;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to commit offsets!", e);
        }
    }

    @Override
    public void unsubscribe() {
        stopped = true;
        delegateConsumer.wakeup();
    }


    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        // 1. Stop all tasks handling records from revoked partitions
        Map<TopicPartition, Task> stoppedTask = new HashMap<>();
        for (var partition : partitions) {
            Task task = activeTasks.remove(partition);
            if (task != null) {
                task.stop();
                stoppedTask.put(partition, task);
            }
        }

        // 2. Wait for stopped tasks to complete processing of current record
        stoppedTask.forEach((partition, task) -> {
            long offset = task.waitForCompletion();
            if (offset > 0) {
                offsetsToCommit.put(partition, new OffsetAndMetadata(offset));
            }
        });

        // 3. Collect offsets for revoked partitions
        Map<TopicPartition, OffsetAndMetadata> revokedPartitionOffsetsReadyToCommit = new HashMap<>();
        partitions.forEach(partition -> {
            var offset = offsetsToCommit.remove(partition);
            if (offset != null) {
                revokedPartitionOffsetsReadyToCommit.put(partition, offset);
            }
        });

        // 4. Commit offset for revoked partitions
        try {
            delegateConsumer.commitSync(revokedPartitionOffsetsReadyToCommit);
        } catch (Exception e) {
            LOGGER.warn("Failed to commit offsets for revoked partitions");
        }
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        delegateConsumer.resume(partitions);
    }
}
