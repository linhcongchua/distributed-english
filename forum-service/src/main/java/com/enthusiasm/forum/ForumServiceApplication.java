package com.enthusiasm.forum;

import com.enthusiasm.common.simple_web.SimpleWebProperties;
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
@EnableConfigurationProperties(SimpleWebProperties.class)
public class ForumServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ForumServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
