package com.enthusiasm.dispatcher.event;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.consumer.MessageSubscription;
import com.enthusiasm.dispatcher.HandlerDescription;
import com.enthusiasm.dispatcher.ListenerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class EventBeanLoader implements SmartInitializingSingleton, BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBeanLoader.class);

    protected final ListenerContainer listenerContainer;

    public EventBeanLoader(ConsumerProperties consumerProperties, ExecutorService executorService) {
        this.listenerContainer = new EventListenerContainer(executorService, consumerProperties);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        EventDispatcher eventDispatcher = aClass.getAnnotation(EventDispatcher.class);
        if (eventDispatcher != null) {
            Map<Method, EventHandler> methodEventDispatcherMap = MethodIntrospector.selectMethods(
                    aClass,
                    (MethodIntrospector.MetadataLookup<EventHandler>) method -> method.getAnnotation(EventHandler.class)
            );

            var handlerDescription = createHandlerDescription(eventDispatcher, methodEventDispatcherMap);
            registerHandler(handlerDescription, bean);
        }

        return bean;
    }

    protected void registerHandler(HandlerDescription handlerDescription, Object bean) {
        listenerContainer.registerListenerContainer(handlerDescription, bean);
    }

    private HandlerDescription createHandlerDescription(EventDispatcher eventDispatcher, Map<Method, EventHandler> methodEventDispatcherMap) {
        var handlerDescription = new HandlerDescription();
        handlerDescription.setTopic(eventDispatcher.topic());
        handlerDescription.setGroup(eventDispatcher.group());
        handlerDescription.setThreadPerPartition(eventDispatcher.isThreadPerPartition());

        final Map<String, Method> eventTypeMethod = new HashMap<>();
        for (var methodHandler : methodEventDispatcherMap.entrySet()) {
            Method method = methodHandler.getKey();
            EventHandler eventHandler = methodHandler.getValue();
            String eventType = eventHandler.eventType();
            eventTypeMethod.put(eventType, method);
        }

        handlerDescription.setMethodHandler(eventTypeMethod);
        return handlerDescription;
    }

    @Override
    public void afterSingletonsInstantiated() {
        LOGGER.info("Starting event listener");
        Set<MessageSubscription> subscriptions = listenerContainer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Stopping event listener");
            subscriptions.forEach(MessageSubscription::unsubscribe);
        }));
    }
}
