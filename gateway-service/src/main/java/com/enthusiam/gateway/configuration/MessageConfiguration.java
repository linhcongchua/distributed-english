package com.enthusiam.gateway.configuration;

import com.enthusiasm.producer.MessageProducer;
import com.enthusiasm.producer.MessageProducerImpl;
import com.enthusiasm.producer.ProducerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MessageConfiguration {
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


}
