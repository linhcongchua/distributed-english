package com.enthusiasm.outbox;

public interface OutboxProperties {
    String getPathEventEntity();

    boolean removeAfterInsert();
}
