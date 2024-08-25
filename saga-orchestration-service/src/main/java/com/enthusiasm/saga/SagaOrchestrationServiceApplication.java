package com.enthusiasm.saga;

import com.enthusiasm.common.simple_web.SimpleWebProperties;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(
        scanBasePackages = {
                "com.enthusiasm.saga",
                "com.enthusiasm.common.simple_web"
        }
)
@EnableConfigurationProperties(SimpleWebProperties.class)
public class SagaOrchestrationServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SagaOrchestrationServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
