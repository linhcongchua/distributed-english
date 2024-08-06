package com.enthusiasm.saga;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SagaOrchestrationServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SagaOrchestrationServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
