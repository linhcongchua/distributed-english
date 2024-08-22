package com.enthusiasm.forum.events;

import com.enthusiasm.common.core.SuccessResponse;
import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.outbox.ExportedEvent;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostCreatePendingEvent implements ExportedEvent<String, JsonNode>, SuccessResponse {
    private final UUID userId;
    private final String replyTopic;
    private final String sagaHeader;

    public PostCreatePendingEvent(UUID userId, String replyTopic, String sagaHeader) {
        this.userId = userId;
        this.replyTopic = replyTopic;
        this.sagaHeader = sagaHeader;
    }

    @Override
    public String getAggregateId() { // key for partition
        return userId.toString();
    }

    @Override
    public String getAggregateType() { // topic
        return replyTopic;
    }

    @Override
    public String getType() { // header extra
        return sagaHeader;
    }

    @Override
    public Instant getTimestamp() {
        return Instant.now();
    }

    @Override
    public JsonNode getPayload() { // value body
        return SerializerUtils.serializeToJsonNode(response());
    }

    @Override
    public Map<String, Object> getAdditionalFieldValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        return map;
    }
}
