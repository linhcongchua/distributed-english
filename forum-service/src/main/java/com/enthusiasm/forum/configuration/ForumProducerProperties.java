package com.enthusiasm.forum.configuration;

import com.enthusiasm.producer.ProducerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("producer")
public record ForumProducerProperties(
        String bootstrapServers
) implements ProducerProperties {
}
