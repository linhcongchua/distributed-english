package com.enthusiasm.dispatcher;

import com.enthusiasm.consumer.*;
import com.enthusiasm.dispatcher.command.CommandListenerContainer;
import com.enthusiasm.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public abstract class ListenerContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListenerContainer.class);

    private final Map<String, MessageConsumer> container = new ConcurrentHashMap<>();

    private final ExecutorService executorService;

    private final ConsumerProperties consumerProperties;

    public ListenerContainer(ExecutorService executorService, ConsumerProperties consumerProperties) {
        this.executorService = executorService;
        this.consumerProperties = consumerProperties;
    }

    public void registerListenerContainer(HandlerDescription handlerDescription, Object bean) {
        MessageConsumer messageConsumer = createMessageConsumer(handlerDescription, bean);
        container.put(handlerDescription.getGroup(), messageConsumer);
    }

    private MessageConsumer createMessageConsumer(HandlerDescription handlerDescription, Object instanceTarget) {
        MessageConsumer messageConsumer = null;
        MessageHandler handler = createHandler(handlerDescription, instanceTarget);
        if (handlerDescription.isThreadPerPartition()) {
            messageConsumer = new MessageConsumerMultiThreadImpl(
                    executorService,
                    consumerProperties,
                    handler,
                    handlerDescription.getTopic());
        } else {
            messageConsumer = new MessageConsumerSingleThreadImpl(
                    consumerProperties,
                    handler,
                    handlerDescription.getTopic());
        }
        return messageConsumer;
    }

    protected abstract MessageHandler createHandler(HandlerDescription handlerDescription, Object instanceTarget);

    public Set<MessageSubscription> start() {
        Set<MessageSubscription> subscriptions = new HashSet<>();
        container.forEach((s, messageConsumer) -> {
            LOGGER.info("Starting listener {}", s); // todo modify s
            MessageSubscription subscription = messageConsumer.subscribe();
            subscriptions.add(subscription);
        });

        return subscriptions;
    }
}
