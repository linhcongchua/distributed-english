package com.enthusiasm.events.exceptions;

public class AggregateNotFoundException extends RuntimeException {
    public AggregateNotFoundException() {
    }

    public AggregateNotFoundException(String aggregateId) {
        super("aggregate not found id: " + aggregateId);
    }
}
