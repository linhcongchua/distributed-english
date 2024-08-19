package com.enthusiasm.saga.loader;

import com.enthusiasm.common.jackson.DeserializerUtils;
import com.enthusiasm.common.jackson.SerializerUtils;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisSagaInstanceRepository implements SagaInstanceRepository {

    private final RedisTemplate<String, byte[]> redisTemplate;

    public RedisSagaInstanceRepository(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <T> T getInstance(String instanceId, Class<T> clazz) {
        // todo: check exists
        byte[] bytes = redisTemplate.opsForValue().get(instanceId);
        return DeserializerUtils.deserialize(bytes, clazz);
    }

    @Override
    public <T> void saveInstance(T instance, String keyId) {
        redisTemplate.opsForValue().set(keyId, SerializerUtils.serializeToJsonBytes(instance));
    }
}
