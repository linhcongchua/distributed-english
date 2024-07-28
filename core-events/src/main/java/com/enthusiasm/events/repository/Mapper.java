package com.enthusiasm.events.repository;

import com.enthusiasm.events.Event;
import com.enthusiasm.events.Snapshot;
import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Mapper {
    public static final RowMapper<Event> EVENT_ROW_MAPPER = (rs, rowNum) -> Event.builder()
            .aggregateId(rs.getString(Constants.AGGREGATE_ID))
            .aggregateType(rs.getString(Constants.AGGREGATE_TYPE))
            .eventType(rs.getString(Constants.EVENT_TYPE))
            .data(rs.getBytes(Constants.DATA))
            .metaData(rs.getBytes(Constants.METADATA))
            .version(rs.getLong(Constants.VERSION))
            .timeStamp(rs.getTimestamp(Constants.TIMESTAMP).toLocalDateTime())
            .build();

    public static final RowMapper<Snapshot> SNAPSHOT_ROW_MAPPER = (rs, rowNum) -> Snapshot.builder()
            .aggregateId(rs.getString(Constants.AGGREGATE_ID))
            .aggregateType(rs.getString(Constants.AGGREGATE_TYPE))
            .data(rs.getBytes(Constants.DATA))
            .metaData(rs.getBytes(Constants.METADATA))
            .version(rs.getLong(Constants.VERSION))
            .timeStamp(rs.getTimestamp(Constants.TIMESTAMP).toLocalDateTime())
            .build();

    public static Map<String, Serializable> mapTo(Event event) {
        return ImmutableMap.<String, Serializable>builder()
                .put(Constants.AGGREGATE_ID, event.getAggregateId())
                .put(Constants.AGGREGATE_TYPE, event.getAggregateType())
                .put(Constants.EVENT_TYPE, event.getEventType())
                .put(Constants.DATA, Objects.isNull(event.getData()) ? new byte[0] : event.getData())
                .put(Constants.METADATA, Objects.isNull(event.getMetaData()) ? new byte[0] : event.getMetaData())
                .put(Constants.VERSION, event.getVersion())
                .build();
    }

    public static Map<String, Serializable> mapTo(String aggregateId, long version) {
        return ImmutableMap.<String, Serializable>builder()
                .put(Constants.AGGREGATE_ID, aggregateId)
                .put(Constants.VERSION, version)
                .build();
    }

    public static Map<String, Serializable> mapTo(String aggregateId) {
        return ImmutableMap.<String, Serializable>builder()
                .put(Constants.AGGREGATE_ID, aggregateId)
                .build();
    }

    public static Map<String, Serializable> mapTo(Snapshot snapshot) {
        return ImmutableMap.<String, Serializable>builder()
                .put(Constants.AGGREGATE_ID, snapshot.getAggregateId())
                .put(Constants.AGGREGATE_TYPE, snapshot.getAggregateType())
                .put(Constants.DATA, Objects.isNull(snapshot.getData()) ? new byte[0] : snapshot.getData())
                .put(Constants.METADATA, Objects.isNull(snapshot.getMetaData()) ? new byte[0] : snapshot.getMetaData())
                .put(Constants.VERSION, snapshot.getVersion())
                .build();
    }
}
