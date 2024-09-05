package com.enthusiasm.dispatcher;

import com.enthusiasm.common.jackson.DeserializerUtils;
import com.enthusiasm.consumer.MessageHandler;
import com.enthusiasm.dispatcher.command.NotFoundCommandTypeHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class DispatcherMessageHandler implements MessageHandler {
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
        Headers headers = record.headers();
        Header commandTypeHeader = headers.lastHeader(getTypeHeader());
        String commandType = new String(commandTypeHeader.value(), StandardCharsets.UTF_8);

        Map<String, Method> methodHandler = handlerDescription.getMethodHandler();
        Method method = methodHandler.get(commandType);
        if (method == null) {
            throw new NotFoundCommandTypeHandler(commandType);
        }
        return method;
    }

    protected abstract String getTypeHeader();

    protected abstract byte[] getValueFromRecord(ConsumerRecord<String, byte[]> record, Parameter parameter);

    protected void handleError(Exception e) {}
}
