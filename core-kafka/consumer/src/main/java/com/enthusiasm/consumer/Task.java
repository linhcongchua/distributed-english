package com.enthusiasm.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

public class Task implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

    private final List<ConsumerRecord<String, byte[]>> records; // in partition
    private final MessageHandler handler;
    private volatile State state = State.CREATED;

    private final CompletableFuture<Long> completion = new CompletableFuture<>();
    private final Object lockKey = new Object();
    private final AtomicLong currentOffset = new AtomicLong();


    public Task(List<ConsumerRecord<String, byte[]>> records, MessageHandler handler) {
        this.records = records;
        this.handler = handler;
    }

    @Override
    public void run() {
        synchronized (lockKey) {
            if (state == State.STOPPED) {
                return;
            }
            state = State.IN_PROCESS;
        }
        for (var record : records) {
            if (state == State.STOPPED) {
                break;
            }
            handler.accept(record);

            currentOffset.set(record.offset() + 1);
        }

        synchronized (lockKey) {
            state = State.FINISHED;
            completion.complete(currentOffset.get());
        }
    }

    public long getCurrentOffset() {
        return currentOffset.get();
    }

    public void stop() {
        synchronized (lockKey) {
            if (state == State.IN_PROCESS) {
                completion.complete(currentOffset.get());
            }
            state = State.STOPPED;
        }
    }

    public long waitForCompletion() {
        try {
            return completion.get();
        } catch (InterruptedException | ExecutionException exception) {
            LOGGER.error("Failed when wait for task complete!", exception);
            return -1;
        }
    }

    public boolean isFinished() {
        return state == State.FINISHED;
    }

    enum State {
        CREATED,
        IN_PROCESS,
        STOPPED,
        FINISHED
    }
}
