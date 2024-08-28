package com.enthusiasm.common.payment.response;

import com.enthusiasm.common.core.SagaResponse;
import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.outbox.ExportedEvent;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HoldAmountResponse implements ExportedEvent<String, JsonNode>, SagaResponse {
    private final String userId;
    private final String replyTopic;
    private final String sagaHeader;

    public HoldAmountResponse(String userId, String replyTopic, String sagaHeader) {
        this.userId = userId;
        this.replyTopic = replyTopic;
        this.sagaHeader = sagaHeader;
    }

    @Override
    public String getAggregateId() {
        return userId;
    }

    @Override
    public String getAggregateType() {
        return replyTopic;
    }

    @Override
    public String getType() {
        return sagaHeader;
    }

    @Override
    public Instant getTimestamp() {
        return Instant.now();
    }

    @Override
    public JsonNode getPayload() { // value body
        return SerializerUtils.serializeToJsonNode(success());
    }

    @Override
    public Map<String, Object> getAdditionalFieldValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        return map;
    }
}
