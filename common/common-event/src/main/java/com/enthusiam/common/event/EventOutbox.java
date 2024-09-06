package com.enthusiam.common.event;

import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.outbox.ExportedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import io.opentelemetry.api.trace.SpanContext;
import lombok.Builder;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Builder
public class EventOutbox<T> implements ExportedEvent<String, JsonNode> {
    private final String key;

    private final String topic;

    private final SpanContext spanContext;
    private final String eventType;

    private final T payload;

    public EventOutbox(String key, String topic, SpanContext spanContext, String eventType, T payload) {
        this.key = key;
        this.topic = topic;
        this.spanContext = spanContext;
        this.eventType = eventType;
        this.payload = payload;
    }

    @Override
    public String getAggregateId() {
        return key;
    }

    @Override
    public String getAggregateType() {
        return topic;
    }

    @Override
    public String getType() { // header
        Map<String, Object> map = new HashMap<>();
        map.put("EVENT_TYPE", eventType);
        return SerializerUtils.serializeToJsonStr(map);
    }

    @Override
    public Instant getTimestamp() {
        return Instant.now();
    }

    @Override
    public JsonNode getPayload() {
        return SerializerUtils.serializeToJsonNode(payload);
    }

    @Override
    public Map<String, Object> getAdditionalFieldValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("tracing", SerializerUtils.serializeToJsonStr(spanContext));
        return map;
    }
}
