package com.enthusiasm.dispatcher;

import com.enthusiasm.common.jackson.DeserializerUtils;
import com.enthusiasm.consumer.MessageHandler;
import com.enthusiasm.dispatcher.command.NotFoundCommandTypeHandler;
import com.enthusiasm.dispatcher.utils.RecordHeaderUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class DispatcherMessageHandler implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherMessageHandler.class);

    private final HandlerDescription handlerDescription;
    private final Object instance;

    public DispatcherMessageHandler(HandlerDescription handlerDescription, Object instance) {
        this.handlerDescription = handlerDescription;
        this.instance = instance;
    }

    @Override
    public void accept(ConsumerRecord<String, byte[]> record) {
        try {
            Method method = getMethod(record);

            Parameter[] parameters = method.getParameters();
            Object[] parametersValue = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                // check body | header
                byte[] value = getValueFromRecord(record, parameter);
                Object parameterValue = DeserializerUtils.deserialize(value, parameter.getType());
                parametersValue[i] = parameterValue;
            }

            method.setAccessible(true);
            method.invoke(instance, parametersValue);
        } catch (Exception e) {
            handleError(e);
        }
    }

    private Method getMethod(ConsumerRecord<String, byte[]> record) {
        String type = RecordHeaderUtils.getHeader(record, "EXTRA_HEADER", getTypeHeader(), String.class);

        Map<String, Method> methodHandler = handlerDescription.getMethodHandler();
        Method method = methodHandler.get(type);
        if (method == null) {
            throw new NotFoundCommandTypeHandler(type);
        }
        return method;
    }

    protected abstract String getTypeHeader();

    protected abstract byte[] getValueFromRecord(ConsumerRecord<String, byte[]> record, Parameter parameter);

    protected void handleError(Exception e) {}
}
