package com.enthusiasm.payment.configuration;

import com.enthusiasm.consumer.ConsumerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class LoaderConfiguration {
    @Bean
    public CommandBeanLoader commandBeanLoader(ConsumerProperties consumerProperties, ExecutorService executorService) {
        return new CommandBeanLoader(consumerProperties, executorService);
    }
}
