package com.enthusiasm.saga.loader;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.saga.core.SagaDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SagaDefinitionLoader implements BeanPostProcessor, ApplicationContextAware, InitializingBean, SmartInitializingSingleton {

    private final ConsumerProperties consumerProperties;

    private final SagaDefinitionContainer sagaDefinitionContainer;

    public SagaDefinitionLoader(ConsumerProperties consumerProperties) {
        this.consumerProperties = consumerProperties;
        this.sagaDefinitionContainer = new SagaDefinitionContainer();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof SagaDefinition<?> sagaDefinition) {
            sagaDefinitionContainer.addDefinition(sagaDefinition);
        }

        return bean;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void afterSingletonsInstantiated() {
        // start listener
        SagaSubscribeManager sagaSubscribeManager = new SagaSubscribeManager(sagaDefinitionContainer.getDefinitions(), consumerProperties);
        sagaSubscribeManager.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
