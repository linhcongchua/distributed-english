package com.enthusiasm.forum.configuration;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.dispatcher.command.CommandBeanLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CommandDispatcherConfiguration {

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(12);
    }

    @Bean
    ConsumerProperties consumerProperties() {
        return new ConsumerProperties() {
            @Override
            public String bootstrapServers() {
                return "localhost:9092";
            }

            @Override
            public String groupId() {
                return "forum-service";
            }
        };
    }


    @Bean
    public CommandBeanLoader commandBeanLoader(ConsumerProperties consumerProperties, ExecutorService executorService) {
        return new CommandBeanLoader(consumerProperties, executorService);
    }
}
