package com.enthusiasm.payment.entities;

import com.enthusiasm.outbox.ExportedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "event_publish")
public class EventPublish implements ExportedEvent<String, JsonNode> {
    @Id
    @Basic
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;
    private String aggregateId;
    private String aggregateType;
    private String type;
    private Instant timestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode payload;

    public EventPublish() {
    }

    public EventPublish(UUID id, String aggregateId, String aggregateType, String type, Instant timestamp, JsonNode payload) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.type = type;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }
}
