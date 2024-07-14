package com.enthusiasm.paymentservice;

import com.enthusiasm.paymentservice.kafka.MultiThreadedKafkaConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
        MultiThreadedKafkaConsumer consumer = new MultiThreadedKafkaConsumer("test-topic");
    }
}
