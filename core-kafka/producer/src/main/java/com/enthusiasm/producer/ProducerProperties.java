package com.enthusiasm.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public interface ProducerProperties {
    String bootstrapServers();

    default String interceptors() {
        return LoggingProducerInterceptor.class.getName();
    }

    default Properties getDefault() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors());
        return props;
    }
}
