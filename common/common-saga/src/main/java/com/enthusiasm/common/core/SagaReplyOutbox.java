package com.enthusiasm.common.core;

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
public class SagaReplyOutbox<T> implements ExportedEvent<String, JsonNode> {
    private final String key;

    private final SpanContext spanContext;

    private final SagaHeader sagaHeader;

    private final T payload;

    public SagaReplyOutbox(String key, SpanContext spanContext, SagaHeader sagaHeader, T payload) {
        this.key = key;
        this.spanContext = spanContext;
        this.sagaHeader = sagaHeader;
        this.payload = payload;
    }

    @Override
    public String getAggregateId() {
        return key;
    }

    @Override
    public String getAggregateType() {
        return sagaHeader.topic();
    }

    @Override
    public String getType() { // header
        Map<String, Object> map = new HashMap<>();
        map.put("SAGA_HEADER", sagaHeader);
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
