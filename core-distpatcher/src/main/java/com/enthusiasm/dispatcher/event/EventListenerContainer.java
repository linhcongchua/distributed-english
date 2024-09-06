package com.enthusiasm.dispatcher.event;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.consumer.MessageHandler;
import com.enthusiasm.dispatcher.HandlerDescription;
import com.enthusiasm.dispatcher.ListenerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class EventListenerContainer extends ListenerContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventListenerContainer.class);

    public EventListenerContainer(ExecutorService executorService, ConsumerProperties consumerProperties) {
        super(executorService, consumerProperties);
    }


    @Override
    protected MessageHandler createHandler(HandlerDescription handlerDescription, Object instanceTarget) {
        return new EventMessageHandler(handlerDescription, instanceTarget);
    }
}
