package com.enthusiasm.notification;

import com.enthusiasm.common.simple_web.SimpleWebProperties;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(
        scanBasePackages = {
                "com.enthusiasm.notification",
                "com.enthusiasm.common.simple_web"
        }
)
@EnableConfigurationProperties(SimpleWebProperties.class)
@EnableKafka
public class NotificationServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(NotificationServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
