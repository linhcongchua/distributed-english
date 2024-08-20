package com.enthusiasm.forum.events;

import com.enthusiasm.outbox.ExportedEvent;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

public class PostCreatePendingEvent implements ExportedEvent<String, JsonNode> {

    @Override
    public String getAggregateId() {
        return null;
    }

    @Override
    public String getAggregateType() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public Instant getTimestamp() {
        return null;
    }

    @Override
    public JsonNode getPayload() {
        return null;
    }
}
