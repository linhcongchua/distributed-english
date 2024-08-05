package com.enthusiasm.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

import java.util.Map;

public abstract class AbstractEventDispatcher extends AbstractEventWriter<Void> implements EventDispatcher {
    private final EntityManager entityManager;
    private final OutboxProperties outboxProperties;

    public AbstractEventDispatcher(EntityManager entityManager, OutboxProperties outboxProperties) {
        this.entityManager = entityManager;
        this.outboxProperties = outboxProperties;
    }

    @Override
    protected Void persist(Map<String, Object> dataMap) {
        // Unwrap to Hibernate session and save
        var session = entityManager.unwrap(Session.class);
        Object entity = JsonMapperUtils.loadObject(dataMap, outboxProperties.getPathEventEntity());
        session.persist(outboxProperties.getPathEventEntity(), entity);
        session.setReadOnly(entity, true);
        remove(session, dataMap);
        return null;
    }

    private void remove(Session session, Map<String, Object> dataMap) {
        if (outboxProperties.removeAfterInsert()) {
            // session.delete(outboxProperties.getPathEventEntity(), dataMap);
            session.remove(dataMap);
        }
    }
}
