package com.enthusiasm.dispatcher.command;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.consumer.MessageSubscription;
import com.enthusiasm.dispatcher.DispatcherBeanLoader;
import com.enthusiasm.dispatcher.HandlerDescription;
import com.enthusiasm.dispatcher.ListenerContainer;
import com.enthusiasm.producer.MessageProducer;
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

public class CommandBeanLoader extends DispatcherBeanLoader implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandBeanLoader.class);

    private final MessageProducer messageProducer;

    public CommandBeanLoader(ConsumerProperties consumerProperties, ExecutorService executorService, MessageProducer messageProducer) {
        super(consumerProperties, executorService);
        this.messageProducer = messageProducer;
    }

    @Override
    protected ListenerContainer initializeListenerContainer(ConsumerProperties consumerProperties, ExecutorService executorService) {
        return new CommandListenerContainer(executorService, messageProducer, consumerProperties);
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
            Map<Method, CommandHandler> methodCommandHandlerMap = MethodIntrospector.selectMethods(
                    aClass,
                    (MethodIntrospector.MetadataLookup<CommandHandler>) method -> method.getAnnotation(CommandHandler.class)
            );

            var handlerDescription = createHandlerDescription(commandDispatcher, methodCommandHandlerMap);
            registerHandler(handlerDescription, bean);
        }

        return bean;
    }

    private HandlerDescription createHandlerDescription(CommandDispatcher commandDispatcher, Map<Method, CommandHandler> methodCommandHandlerMap) {
        var handlerDescription = new HandlerDescription();
        handlerDescription.setTopic(commandDispatcher.service() + '-' + commandDispatcher.topic()); // todo: fix
        handlerDescription.setGroup(commandDispatcher.service()); // todo: check logic
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
}
