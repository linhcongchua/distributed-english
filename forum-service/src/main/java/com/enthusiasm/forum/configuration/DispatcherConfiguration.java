package com.enthusiasm.forum.configuration;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.dispatcher.command.CommandBeanLoader;
import com.enthusiasm.dispatcher.event.EventBeanLoader;
import com.enthusiasm.outbox.DefaultEventDispatcher;
import com.enthusiasm.outbox.EventDispatcher;
import com.enthusiasm.outbox.OutboxProperties;
import com.enthusiasm.producer.MessageProducer;
import com.enthusiasm.producer.MessageProducerImpl;
import com.enthusiasm.producer.ProducerProperties;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class DispatcherConfiguration {

    private final ForumConsumerProperties forumConsumerProperties;
    private final ForumProducerProperties forumProducerProperties;
    private final ForumOutboxProperties forumOutboxProperties;

    public DispatcherConfiguration(ForumConsumerProperties forumConsumerProperties, ForumProducerProperties forumProducerProperties, ForumOutboxProperties forumOutboxProperties) {
        this.forumConsumerProperties = forumConsumerProperties;
        this.forumProducerProperties = forumProducerProperties;
        this.forumOutboxProperties = forumOutboxProperties;
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    EventDispatcher eventDispatcher(EntityManager entityManager) {
        return new DefaultEventDispatcher(entityManager, forumOutboxProperties);
    }

    @Bean
    MessageProducer messageProducer() {
        return new MessageProducerImpl(forumProducerProperties);
    }


    @Bean
    public CommandBeanLoader commandBeanLoader() {
        return new CommandBeanLoader(forumConsumerProperties, executorService(), messageProducer());
    }

    @Bean
    public EventBeanLoader eventBeanLoader() {
        return new EventBeanLoader(forumConsumerProperties, executorService());
    }
}
