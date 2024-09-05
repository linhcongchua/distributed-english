package com.enthusiasm.forum;

import com.enthusiasm.common.simple_web.SimpleWebProperties;
import com.enthusiasm.forum.configuration.ForumConsumerProperties;
import com.enthusiasm.forum.configuration.ForumOutboxProperties;
import com.enthusiasm.forum.configuration.ForumProducerProperties;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(
        scanBasePackages = {
                "com.enthusiasm.forum",
                "com.enthusiasm.common.simple_web"
        }
)
@EnableConfigurationProperties(
        {
                SimpleWebProperties.class,
                ForumConsumerProperties.class,
                ForumProducerProperties.class,
                ForumOutboxProperties.class
        }
)
public class ForumServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ForumServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
