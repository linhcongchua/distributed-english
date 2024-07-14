package com.enthusiasm.accountservice;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AccountServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
