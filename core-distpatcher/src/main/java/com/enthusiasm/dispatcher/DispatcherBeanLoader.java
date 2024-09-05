package com.enthusiasm.dispatcher;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.consumer.MessageSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.Set;
import java.util.concurrent.ExecutorService;

public abstract class DispatcherBeanLoader implements SmartInitializingSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherBeanLoader.class);

    protected final ListenerContainer listenerContainer;

    public DispatcherBeanLoader(ConsumerProperties consumerProperties, ExecutorService executorService) {
        this.listenerContainer = initializeListenerContainer(consumerProperties, executorService);
    }

    protected abstract ListenerContainer initializeListenerContainer(ConsumerProperties consumerProperties, ExecutorService executorService);

    protected void registerHandler(HandlerDescription handlerDescription, Object bean) {
        listenerContainer.registerListenerContainer(handlerDescription, bean);
    }

    @Override
    public void afterSingletonsInstantiated() {
        LOGGER.info("Starting listener");
        Set<MessageSubscription> subscriptions = listenerContainer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                subscriptions.forEach(MessageSubscription::unsubscribe))); // todo shutdown hook?
    }
}
