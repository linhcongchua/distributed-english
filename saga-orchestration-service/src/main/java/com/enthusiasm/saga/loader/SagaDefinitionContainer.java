package com.enthusiasm.saga.loader;

import com.enthusiasm.saga.core.SagaDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SagaDefinitionContainer {
    private final Map<String, SagaDefinition<?>> container = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    protected void addDefinition(SagaDefinition<?> sagaDefinition) {
        try {
            writeLock.lock();
            container.put(sagaDefinition.id(), sagaDefinition);
        } finally {
            writeLock.unlock();
        }
    }

    protected SagaDefinition<?> getDefinition(String id) {
        try {
            readLock.lock();
            return container.get(id);
        } finally {
            readLock.unlock();
        }
    }

    protected List<SagaDefinition<?>> getDefinitions() {
        try {
            readLock.lock();
            return new ArrayList<>(container.values());
        } finally {
            readLock.unlock();
        }
    }
}
