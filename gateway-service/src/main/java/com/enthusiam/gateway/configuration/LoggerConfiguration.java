package com.enthusiam.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class LoggerConfiguration {
    @Bean
    public CommonsRequestLoggingFilter loggingFilter() {
        final var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10_000);
        filter.setIncludeHeaders(true);
        filter.setBeforeMessagePrefix("---> Received request: ");
        return filter;
    }
}
