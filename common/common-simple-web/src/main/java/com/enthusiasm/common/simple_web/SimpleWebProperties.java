package com.enthusiasm.common.simple_web;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("simple.web")
public record SimpleWebProperties(
        int port
) {
}
