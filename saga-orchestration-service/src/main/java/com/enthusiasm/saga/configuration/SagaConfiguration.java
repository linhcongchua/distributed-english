package com.enthusiasm.saga.configuration;

import com.enthusiasm.consumer.ConsumerProperties;
import com.enthusiasm.producer.MessageProducer;
import com.enthusiasm.producer.MessageProducerImpl;
import com.enthusiasm.producer.ProducerProperties;
import com.enthusiasm.saga.loader.RedisSagaInstanceRepository;
import com.enthusiasm.saga.loader.SagaDefinitionLoader;
import com.enthusiasm.saga.loader.SagaInstanceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

@Configuration
public class SagaConfiguration {

    @Bean
    public ConsumerProperties consumerProperties() {
        return new ConsumerProperties() {
            @Override
            public String bootstrapServers() {
                return "localhost:9092";
            }

            @Override
            public String groupId() {
                return "saga-orchestration-service";
            }
        };
    }

    @Bean
    public ProducerProperties producerProperties() {
        return new ProducerProperties() {
            @Override
            public String bootstrapServers() {
                return "localhost:9092";
            }
        };
    }

    @Bean
    public SagaInstanceRepository sagaInstanceRepository(RedisTemplate<String, byte[]> redisTemplate) {
        return new RedisSagaInstanceRepository(redisTemplate);
    }

    @Bean
    public MessageProducer messageProducer() {
        var messageProducer = new MessageProducerImpl(producerProperties());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                messageProducer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        return messageProducer;
    }

    @Bean
    public SagaDefinitionLoader sagaDefinitionLoader(RedisTemplate<String, byte[]> redisTemplate) {
        return new SagaDefinitionLoader(
                consumerProperties(),
                sagaInstanceRepository(redisTemplate),
                messageProducer()
        );
    }
}
