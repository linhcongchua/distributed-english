package com.enthusiasm.payment.configuration;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.dispatcher.command.CommandBeanLoader;
import com.enthusiasm.events.repository.EventRepository;
import com.enthusiasm.events.repository.EventRepositoryImpl;
import com.enthusiasm.outbox.DefaultEventDispatcher;
import com.enthusiasm.outbox.EventDispatcher;
import com.enthusiasm.outbox.OutboxProperties;
import com.enthusiasm.producer.MessageProducer;
import com.enthusiasm.producer.MessageProducerImpl;
import com.enthusiasm.producer.ProducerProperties;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CommandDispatcherConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDispatcherConfiguration.class);

    @Bean
    public ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
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
                return "payment-service";
            }
        };
    }

    @Bean
    ProducerProperties producerProperties() {
        return new ProducerProperties() {
            @Override
            public String bootstrapServers() {
                return "localhost:9092";
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

    @Bean
    MessageProducer messageProducer(ProducerProperties producerProperties) {
        return new MessageProducerImpl(producerProperties);
    }


    @Bean
    public CommandBeanLoader commandBeanLoader(ConsumerProperties consumerProperties, ExecutorService executorService, MessageProducer messageProducer) {
        return new CommandBeanLoader(consumerProperties, executorService, messageProducer);
    }
}
