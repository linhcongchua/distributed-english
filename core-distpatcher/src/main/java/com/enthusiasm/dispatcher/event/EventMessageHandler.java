package com.enthusiasm.dispatcher.event;

import com.enthusiasm.dispatcher.DispatcherMessageHandler;
import com.enthusiasm.dispatcher.HandlerDescription;
import com.enthusiasm.dispatcher.command.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Parameter;

public class EventMessageHandler extends DispatcherMessageHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(EventMessageHandler.class);

    public EventMessageHandler(HandlerDescription handlerDescription, Object instance) {
        super(handlerDescription, instance);
    }

    @Override
    protected String getTypeHeader() {
        return Constants.EVENT_TYPE;
    }

    @Override
    protected byte[] getValueFromRecord(ConsumerRecord<String, byte[]> record, Parameter parameter) {
        EventBody eventBody = parameter.getAnnotation(EventBody.class);
        if (eventBody != null) {
            return record.value();
        }
        throw new RuntimeException("Cannot detect the parameter value!");
    }

    @Override
    protected void handleError(Exception e) {
        LOGGER.error("Error when handle message", e);
    }
}
