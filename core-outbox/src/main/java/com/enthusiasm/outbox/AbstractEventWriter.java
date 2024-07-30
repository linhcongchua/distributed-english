package com.enthusiasm.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEventWriter<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEventWriter.class);

    protected static final String AGGREGATE_ID = "aggregateId";
    protected static final String AGGREGATE_TYPE = "aggregateType";
    protected static final String TYPE = "type";
    protected static final String PAYLOAD = "payload";
    protected static final String TIMESTAMP = "timestamp";

    protected abstract T persist(Map<String, Object> dataMap);

    protected Map<String, Object> getDataMapFromEvent(ExportedEvent<?,?> event) {
        final Map<String, Object> dataMap = createDataMap(event);

        for (var additionField : event.getAdditionalFieldValues().entrySet()) {
            if (dataMap.containsKey(additionField.getKey())) {
                LOGGER.warn("Outbox entity already contains field with name '{}', additional field mapping skipped", additionField.getKey());
                continue;
            }
            dataMap.put(additionField.getKey(), additionField.getValue());
        }

        return dataMap;
    }

    protected Map<String, Object> createDataMap(ExportedEvent<?, ?> event) {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(AGGREGATE_ID, event.getAggregateId());
        dataMap.put(AGGREGATE_TYPE, event.getAggregateType());
        dataMap.put(TYPE, event.getType());
        dataMap.put(PAYLOAD, event.getPayload());
        dataMap.put(TIMESTAMP, event.getTimestamp());
        return dataMap;
    }
}
