package com.enthusiam.gateway.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("forum.gprc")
public record ForumProperties(
        String endpoint
) {
}
