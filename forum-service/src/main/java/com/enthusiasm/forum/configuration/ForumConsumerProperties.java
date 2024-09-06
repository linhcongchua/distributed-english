package com.enthusiasm.forum.configuration;

import com.enthusiasm.consumer.ConsumerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("consumer")
public record ForumConsumerProperties(
        String bootstrapServers,
        String groupId
) implements ConsumerProperties {
}
