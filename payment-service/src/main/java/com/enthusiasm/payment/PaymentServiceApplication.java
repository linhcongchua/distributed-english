package com.enthusiasm.payment;

import com.enthusiasm.common.simple_web.SimpleWebProperties;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
        scanBasePackages = {
                "com.enthusiasm.payment",
                "com.enthusiasm.common.simple_web"
        }
)
@EnableConfigurationProperties(SimpleWebProperties.class)
public class PaymentServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(PaymentServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
