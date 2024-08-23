package com.enthusiasm.saga.loader;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.consumer.MessageConsumer;
import com.enthusiasm.consumer.MessageConsumerSingleThreadImpl;
import com.enthusiasm.producer.MessageProducer;
import com.enthusiasm.saga.core.SagaDefinition;

import java.util.ArrayList;
import java.util.List;

public class SagaSubscribeManager {

    private final List<MessageConsumer> messageConsumers;
    private final ConsumerProperties consumerProperties;

    private final SagaInstanceRepository sagaInstanceRepository;

    private final MessageProducer messageProducer;

    public SagaSubscribeManager(List<SagaDefinition<?>> sagaDefinitions, ConsumerProperties consumerProperties, SagaInstanceRepository sagaInstanceRepository, MessageProducer messageProducer) {
        this.consumerProperties = consumerProperties;
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.messageProducer = messageProducer;
        this.messageConsumers = createMessageConsumer(sagaDefinitions);
    }

    private List<MessageConsumer> createMessageConsumer(List<SagaDefinition<?>> sagaDefinitions) {
        List<MessageConsumer> consumers = new ArrayList<>(sagaDefinitions.size());
        sagaDefinitions.forEach(sagaDefinition -> {
            final SagaMessageHandler<?, ?> sagaMessageHandler = new SagaMessageHandler<>(sagaDefinition, sagaInstanceRepository, messageProducer);

            MessageConsumer consumer = new MessageConsumerSingleThreadImpl(
                    consumerProperties,
                    sagaMessageHandler,
                    sagaDefinition.topic()
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
