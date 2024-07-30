package com.enthusiasm.events;

import com.enthusiasm.outbox.ExportedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event implements ExportedEvent<String, JsonNode> {
    private UUID id;
    private String aggregateId;
    private String eventType;
    private String aggregateType;
    private long version;
    private byte[] data;
    private byte[] metaData;
    private Instant timeStamp;

    public Event(String eventType, String aggregateType) {
        this.id = UUID.randomUUID();
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.timeStamp = Instant.now();
    }

    @Override
    public String getType() {
        return aggregateType;
    }

    @Override
    public Instant getTimestamp() {
        return timeStamp;
    }

    @Override
    public JsonNode getPayload() {
        return SerializerUtils.serializeToJsonNode(data);
    }
}
