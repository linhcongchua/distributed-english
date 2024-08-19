package com.enthusiasm.saga.loader;

public interface SagaInstanceRepository {

    <T> T getInstance(String instanceId, Class<T> clazz);

    <T> void saveInstance(T instance, String keyId);
}
