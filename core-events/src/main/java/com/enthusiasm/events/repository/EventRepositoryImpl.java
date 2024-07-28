package com.enthusiasm.events.repository;

import com.enthusiasm.events.AggregateRoot;
import com.enthusiasm.events.Event;
import com.enthusiasm.events.EventSourcingUtils;
import com.enthusiasm.events.Snapshot;
import com.enthusiasm.events.exceptions.AggregateNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EventRepositoryImpl implements EventRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventRepositoryImpl.class);

    private static final int SNAPSHOT_FREQUENCE = 3;
    private static final String SAVE_EVENTS_QUERY =
            """
                    INSERT INTO events (aggregate_id, aggregate_type, event_type, data, metadata, version, timestamp) 
                    values (:aggregate_id, :aggregate_type, :event_type, :data, :metadata, :version, now())""";
    private static final String LOAD_EVENTS_QUERY =
            """
                    SELECT event_id ,aggregate_id, aggregate_type, event_type, data, metadata, version, timestamp 
                    FROM events e 
                    WHERE e.aggregate_id = :aggregate_id AND e.version > :version ORDER BY e.version ASC""";
    private static final String SAVE_SNAPSHOT_QUERY =
            """
                    INSERT INTO snapshots (aggregate_id, aggregate_type, data, metadata, version, timestamp) VALUES 
                    (:aggregate_id, :aggregate_type, :data, :metadata, :version, now()) 
                    ON CONFLICT (aggregate_id) DO UPDATE SET data = :data, version = :version, timestamp = now()""";
    private static final String HANDLE_CONCURRENCY_QUERY = "SELECT aggregate_id FROM events e WHERE e.aggregate_id = :aggregate_id LIMIT 1 FOR UPDATE";
    private static final String LOAD_SNAPSHOT_QUERY = "SELECT aggregate_id, aggregate_type, data, metadata, version, timestamp FROM snapshots s WHERE s.aggregate_id = :aggregate_id";
    private static final String EXISTS_QUERY = "SELECT aggregate_id FROM events WHERE e e.aggregate_id = :aggregate_id";


    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EventRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void saveEvents(List<Event> events) {
        if (CollectionUtils.isEmpty(events)) {
            return;
        }

        final List<Event> changes = new ArrayList<>(events);
        if (changes.size() > 1) {
            this.eventsBatchInsert(changes);
            return;
        }

        final Event event = changes.get(0);
        int result = jdbcTemplate.update(SAVE_EVENTS_QUERY, Mapper.mapTo(event));
        LOGGER.info("(saveEvents) saved result: {}, event: {}", result, event);
    }

    private void eventsBatchInsert(List<Event> events) {
        final var args = events.stream().map(Mapper::mapTo).toList();
        final Map<String, ?>[] maps = args.toArray(new Map[0]);
        int[] results = jdbcTemplate.batchUpdate(SAVE_EVENTS_QUERY, maps);
        LOGGER.info("(saveEvents) BATCH saved result: {}, event: {}", results, events);
    }


    @Override
    public List<Event> loadEvents(String aggregateId, long version) {
        return jdbcTemplate.query(LOAD_EVENTS_QUERY, Mapper.mapTo(aggregateId, version), Mapper.EVENT_ROW_MAPPER);
    }

    @Override
    @Transactional
    public <T extends AggregateRoot> void save(T aggregate) {
        final List<Event> aggregateEvents = new ArrayList<>(aggregate.getChanges()); // todo: modify kafka

        if (aggregate.getVersion() > 1) {
            handleConcurrency(aggregate.getId());
        }

        saveEvents(aggregateEvents);
        if (aggregate.getVersion() % SNAPSHOT_FREQUENCE == 0) {
            saveSnapshot(aggregate);
        }
        LOGGER.info("(save) saved aggregate: {}", aggregate);
    }

    private <T extends AggregateRoot> void saveSnapshot(T aggregate) {
        aggregate.toSnapshot();
        final var snapshot = EventSourcingUtils.snapshotFromAggregate(aggregate);

        int updateResult = jdbcTemplate.update(SAVE_SNAPSHOT_QUERY, Mapper.mapTo(snapshot));
        LOGGER.info("(saveSnapshot) updateResult: {}", updateResult);
    }

    private void handleConcurrency(String aggregateId) {
        try {
            String aggregateID = jdbcTemplate.queryForObject(HANDLE_CONCURRENCY_QUERY, Mapper.mapTo(aggregateId), String.class);

        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("(handleConcurrency) EmptyResultDataAccessException.", e);
        }
        LOGGER.info("(handleConcurrency) aggregateID for lock: {}", aggregateId);
    }

    @Override
    public <T extends AggregateRoot> T load(String aggregateId, Class<T> aggregateType) {
        final Optional<Snapshot> snapshot = loadSnapshot(aggregateId);

        final var aggregate = getSnapshotFromClass(snapshot, aggregateId, aggregateType);

        final List<Event> events = loadEvents(aggregateId, aggregate.getVersion());
        events.forEach(event -> {
            aggregate.raiseEvent(event);
            LOGGER.info("Raise event version: {}", event.getVersion());
        });

        if (aggregate.getVersion() == 0) {
            throw new AggregateNotFoundException(aggregateId);
        }

        LOGGER.info("(load) loaded aggregate: {}", aggregate);
        return aggregate;
    }

    private <T extends AggregateRoot> T getSnapshotFromClass(Optional<Snapshot> snapshot, String aggregateId, Class<T> aggregateType) {
        if (snapshot.isEmpty()) {
            final var aggregate = getAggregate(aggregateId, aggregateType);
            final var defaultSnapshot = EventSourcingUtils.snapshotFromAggregate(aggregate);
            return EventSourcingUtils.aggregateFromSnapshot(defaultSnapshot, aggregateType);
        }
        return EventSourcingUtils.aggregateFromSnapshot(snapshot.get(), aggregateType);
    }

    private <T extends AggregateRoot> T getAggregate(String aggregateId, Class<T> aggregateType) {
        try {
            return aggregateType.getConstructor(String.class).newInstance(aggregateId); // todo add constraint or not
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Snapshot> loadSnapshot(String aggregateId) {
        return jdbcTemplate.query(LOAD_SNAPSHOT_QUERY, Mapper.mapTo(aggregateId), Mapper.SNAPSHOT_ROW_MAPPER)
                .stream().findFirst();
    }


    @Override
    public boolean exists(String aggregateId) {
        try {
            final var id = jdbcTemplate.queryForObject(EXISTS_QUERY, Mapper.mapTo(aggregateId), String.class);
            LOGGER.info("Aggregate exists id: {}", id);
            return true;
        } catch (Exception ex) {
            if (!(ex instanceof EmptyResultDataAccessException)) {
                throw new RuntimeException(ex);
            }
        }
        return false;
    }
}
