package com.enthusiam.gateway.configuration;

import io.grpc.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class GrpcConfiguration {

    private final ForumProperties forumProperties;

    public GrpcConfiguration(ForumProperties forumProperties) {
        this.forumProperties = forumProperties;
    }

    @Bean
    Channel forumChannel() {
        ChannelCredentials serverCredentials = InsecureChannelCredentials.create();
        return Grpc.newChannelBuilder(forumProperties.endpoint(), serverCredentials)
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
    }
}
