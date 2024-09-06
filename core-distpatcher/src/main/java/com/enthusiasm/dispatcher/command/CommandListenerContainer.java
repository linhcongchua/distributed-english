package com.enthusiasm.dispatcher.command;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.consumer.MessageHandler;
import com.enthusiasm.dispatcher.HandlerDescription;
import com.enthusiasm.dispatcher.ListenerContainer;
import com.enthusiasm.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class CommandListenerContainer extends ListenerContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListenerContainer.class);

    private MessageProducer messageProducer;

    public CommandListenerContainer(ExecutorService executorService, MessageProducer messageProducer, ConsumerProperties consumerProperties) {
        super(executorService, consumerProperties);
        this.messageProducer = messageProducer;
    }

    public void setMessageProducer(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @Override
    protected MessageHandler createHandler(HandlerDescription handlerDescription, Object instanceTarget) {
        return new CommandMessageHandler(handlerDescription, instanceTarget, messageProducer);
    }
}
