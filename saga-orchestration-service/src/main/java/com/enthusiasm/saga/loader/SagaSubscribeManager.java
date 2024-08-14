package com.enthusiasm.saga.loader;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.consumer.MessageConsumer;
import com.enthusiasm.consumer.MessageConsumerSingleThreadImpl;
import com.enthusiasm.saga.core.SagaDefinition;

import java.util.ArrayList;
import java.util.List;

public class SagaSubscribeManager {

    private final List<MessageConsumer> messageConsumers;
    private final ConsumerProperties consumerProperties;

    public SagaSubscribeManager(List<SagaDefinition<?>> sagaDefinitions, ConsumerProperties consumerProperties) {
        this.messageConsumers = createMessageConsumer(sagaDefinitions);
        this.consumerProperties = consumerProperties;
    }

    private List<MessageConsumer> createMessageConsumer(List<SagaDefinition<?>> sagaDefinitions) {
        List<MessageConsumer> consumers = new ArrayList<>(sagaDefinitions.size());
        sagaDefinitions.forEach(sagaDefinition -> {
            final SagaMessageHandler<?> sagaMessageHandler = new SagaMessageHandler<>(sagaDefinition, null, null);

            MessageConsumer consumer = new MessageConsumerSingleThreadImpl(
                    consumerProperties,
                    sagaMessageHandler,
                    ""
                    );
            consumers.add(consumer);
        });

        return consumers;
    }

    public void start() {
        messageConsumers.forEach(MessageConsumer::subscribe);
    }

    public void stop() {
        // todo
    }
}
