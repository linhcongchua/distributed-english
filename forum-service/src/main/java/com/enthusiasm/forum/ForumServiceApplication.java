package com.enthusiasm.forum;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ForumServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ForumServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
