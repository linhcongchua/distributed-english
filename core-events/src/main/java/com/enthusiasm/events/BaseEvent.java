package com.enthusiasm.events;

import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
public class BaseEvent {
    protected String aggregateId;

    public BaseEvent(String aggregateId) {
        Objects.requireNonNull(aggregateId);
        if (aggregateId.isBlank()) {
            throw new RuntimeException("BaseEvent aggregateId is required");
        }
        this.aggregateId = aggregateId;
    }
}
