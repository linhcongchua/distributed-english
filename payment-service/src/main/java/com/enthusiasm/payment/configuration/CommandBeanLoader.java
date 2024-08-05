package com.enthusiasm.payment.configuration;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.consumer.MessageSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

// todo add shutdown graceful
public class CommandBeanLoader implements BeanPostProcessor, ApplicationContextAware, InitializingBean, SmartInitializingSingleton {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandBeanLoader.class);
    private final CommandListenerContainer listenerContainer;

    private final ConsumerProperties consumerProperties;

    public CommandBeanLoader(ConsumerProperties consumerProperties, ExecutorService executorService) {
        this.consumerProperties = consumerProperties;
        listenerContainer = new CommandListenerContainer(executorService);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> aClass = bean.getClass();
        CommandDispatcher commandDispatcher = aClass.getAnnotation(CommandDispatcher.class);
        if (commandDispatcher != null) {
            Map<Method, CommandHandler> methodCommandHandlerMap = MethodIntrospector.selectMethods(aClass, (MethodIntrospector.MetadataLookup<CommandHandler>) method -> method.getAnnotation(CommandHandler.class));

            var handlerDescription = createHandlerDescription(commandDispatcher, methodCommandHandlerMap);
            for (var entry : methodCommandHandlerMap.entrySet()) {
                Method method = entry.getKey();
                CommandHandler commandHandler = entry.getValue();
                registerCommandHandler(handlerDescription, bean);
            }
        }

        return bean;
    }

    private CommandHandlerDescription createHandlerDescription(CommandDispatcher commandDispatcher, Map<Method, CommandHandler> methodCommandHandlerMap) {
        var handlerDescription = new CommandHandlerDescription();
        handlerDescription.setTopic(commandDispatcher.service() + '-' + commandDispatcher.aggregate());
        handlerDescription.setGroup(commandDispatcher.service());
        handlerDescription.setThreadPerPartition(commandDispatcher.isThreadPerPartition()); // todo: should using properties config consumer

        final Map<String, Method> commandTypeMethod = new HashMap<>();
        for (var methodHandler : methodCommandHandlerMap.entrySet()) {
            Method method = methodHandler.getKey();
            CommandHandler commandHandler = methodHandler.getValue();
            String commandType = commandHandler.commandType();
            commandTypeMethod.put(commandType, method);
        }

        handlerDescription.setMethodHandler(commandTypeMethod);
        return handlerDescription;
    }

    private synchronized void registerCommandHandler(CommandHandlerDescription handlerDescription, Object bean) {
        listenerContainer.registerListenerContainer(handlerDescription, bean, consumerProperties);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.info("[Here u are] <<<<<<<<<< setApplicationContext");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("[Here u are] <<<<<<<<<< afterPropertiesSet");
    }

    @Override
    public void afterSingletonsInstantiated() {
        // todo: start consumer
        LOGGER.info("[Here u are] <<<<<<<<<< afterSingletonsInstantiated");
        Set<MessageSubscription> subscriptions = listenerContainer.start();

        //
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            subscriptions.forEach(MessageSubscription::unsubscribe);
        }));
    }
}
