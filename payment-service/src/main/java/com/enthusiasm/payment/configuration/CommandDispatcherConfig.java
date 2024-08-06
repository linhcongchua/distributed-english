package com.enthusiasm.payment.configuration;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.events.repository.EventRepository;
import com.enthusiasm.events.repository.EventRepositoryImpl;
import com.enthusiasm.outbox.DefaultEventDispatcher;
import com.enthusiasm.outbox.EventDispatcher;
import com.enthusiasm.outbox.OutboxProperties;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CommandDispatcherConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDispatcherConfig.class);

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(12);
    }

    @Bean
    ConsumerProperties consumerProperties() {
        return new ConsumerProperties() {
            @Override
            public String bootstrapServers() {
                return "localhost:9092";
            }

            @Override
            public String groupId() {
                return "whatever";
            }
        };
    }

    @Bean
    EventDispatcher eventDispatcher(EntityManager entityManager) {
        return new DefaultEventDispatcher(entityManager, new OutboxProperties() {
            @Override
            public String getPathEventEntity() {
                return "com.enthusiasm.payment.entities.EventPublish";
            }

            @Override
            public boolean removeAfterInsert() {
                return false;
            }
        });
    }

    @Bean
    EventRepository eventRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, EventDispatcher eventDispatcher) {
        return new EventRepositoryImpl(namedParameterJdbcTemplate, eventDispatcher);
    }
}
