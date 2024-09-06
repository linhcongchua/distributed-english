package com.enthusiasm.forum.configuration;

import com.enthusiasm.outbox.OutboxProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("outbox")
public record ForumOutboxProperties(
        String getPathEventEntity,
        boolean removeAfterInsert
) implements OutboxProperties {
}
