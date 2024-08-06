package com.enthusiasm.events;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@Data
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
