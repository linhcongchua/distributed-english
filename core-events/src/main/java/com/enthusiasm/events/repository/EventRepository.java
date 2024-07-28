package com.enthusiasm.events.repository;

import com.enthusiasm.events.AggregateRoot;
import com.enthusiasm.events.Event;

import java.util.List;

public interface EventRepository {
    void saveEvents(final List<Event> events);
    List<Event> loadEvents(final String aggregateId, long version);
    <T extends AggregateRoot> void save(final T aggregate);
    <T extends AggregateRoot> T load(final String aggregateId, final Class<T> aggregateType);
    boolean exists(final String aggregateId);
}
