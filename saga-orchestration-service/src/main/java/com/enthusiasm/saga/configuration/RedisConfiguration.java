package com.enthusiasm.saga.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisConfiguration {
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        final var configuration = new RedisStandaloneConfiguration();
        configuration.setPort(16379);
        configuration.setPassword("redis_password");

        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
