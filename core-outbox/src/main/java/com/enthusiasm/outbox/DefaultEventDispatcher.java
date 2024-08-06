package com.enthusiasm.outbox;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEventDispatcher extends AbstractEventDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventDispatcher.class);

    public DefaultEventDispatcher(EntityManager entityManager, OutboxProperties outboxProperties) {
        super(entityManager, outboxProperties);
    }

    @Override
    public void onExportedEvent(ExportedEvent<?, ?> event) {
        LOGGER.debug("An exported event was found for type {}", event.getType());
        persist(getDataMapFromEvent(event));
    }
}
