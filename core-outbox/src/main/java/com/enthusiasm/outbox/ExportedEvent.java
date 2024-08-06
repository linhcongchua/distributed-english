package com.enthusiasm.outbox;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public interface ExportedEvent<I, P> {
    I getAggregateId();

    String getAggregateType();
    String getType();
    Instant getTimestamp();
    P getPayload();
    default Map<String, Object> getAdditionalFieldValues() {
        return Collections.emptyMap();
    }
}
