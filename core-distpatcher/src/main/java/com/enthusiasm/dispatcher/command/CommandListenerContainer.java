package com.enthusiasm.dispatcher.command;

import com.enthusiasm.common.jackson.DeserializerUtils;
import com.enthusiasm.consumer.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class CommandListenerContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListenerContainer.class);

    private final Map<String, MessageConsumer> container = new ConcurrentHashMap<>();

    private final ExecutorService executorService;

    public CommandListenerContainer(ExecutorService executorService) {
        this.executorService = executorService;
    }

    protected void registerListenerContainer(CommandHandlerDescription handlerDescription, Object bean, ConsumerProperties consumerProperties) {

        MessageConsumer messageConsumer = createMessageConsumer(handlerDescription, bean, consumerProperties);
        container.put(handlerDescription.getGroup(), messageConsumer);
    }

    private MessageConsumer createMessageConsumer(CommandHandlerDescription handlerDescription, Object instanceTarget, ConsumerProperties consumerProperties) {
        MessageConsumer messageConsumer = null;
        CommandMessageHandler handler = new CommandMessageHandler(handlerDescription, instanceTarget);
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

    private static class CommandMessageHandler implements MessageHandler {
        private final CommandHandlerDescription handlerDescription;
        private final Object instance;

        public CommandMessageHandler(CommandHandlerDescription handlerDescription, Object instance) {
            this.handlerDescription = handlerDescription;
            this.instance = instance;
        }

        @Override
        public void accept(ConsumerRecord<String, byte[]> record) {
            try {
                Headers headers = record.headers();
                Header commandTypeHeader = headers.lastHeader("COMMAND_TYPE"); // todo: config debezium header router
                String  commandType = new String(commandTypeHeader.value(), StandardCharsets.UTF_8);
                Map<String, Method> methodHandler = handlerDescription.getMethodHandler();
                Method method = methodHandler.get(commandType);
                if (method == null) {
                    throw new NotFoundCommandTypeHandler(commandType);
                }
                Parameter[] parameters = method.getParameters();
                Parameter parameter = parameters[0];
                Object parameter1 = DeserializerUtils.deserialize(record.value(), parameter.getType());
                method.setAccessible(true);
                method.invoke(instance, parameter1);
            } catch (Exception e) {
                LOGGER.error("Error when handle message", e);
            }
        }
    }

    protected Set<MessageSubscription> start() {
        Set<MessageSubscription> subscriptions = new HashSet<>();
        container.forEach((s, messageConsumer) -> {
            LOGGER.info("Starting listener {}", s); // todo modify s
            MessageSubscription subscription = messageConsumer.subscribe();
            subscriptions.add(subscription);
        });

        return subscriptions;
    }
}
