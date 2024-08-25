package com.enthusiasm.events;

import com.enthusiasm.common.jackson.SerializerUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventSourcingUtils {
    private EventSourcingUtils() {
    }

    public static <T extends AggregateRoot> Snapshot snapshotFromAggregate(final T aggregate) {
        byte[] bytes = SerializerUtils.serializeToJsonBytes(aggregate);
        return Snapshot.builder()
                .id(UUID.randomUUID())
                .aggregateId(aggregate.getId())
                .aggregateType(aggregate.getType())
                .version(aggregate.getVersion())
                .data(bytes)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public static <T extends AggregateRoot> T aggregateFromSnapshot(final Snapshot snapshot, final Class<T> valueType) {
        return SerializerUtils.deserializeFromJsonBytes(snapshot.getData(), valueType);
    }
}
