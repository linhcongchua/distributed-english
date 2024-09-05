package com.enthusiasm.dispatcher.command;

import com.enthusiasm.dispatcher.DispatcherMessageHandler;
import com.enthusiasm.dispatcher.HandlerDescription;
import com.enthusiasm.producer.MessageProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Parameter;

public class CommandMessageHandler extends DispatcherMessageHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommandMessageHandler.class);

    private final MessageProducer messageProducer;

    public CommandMessageHandler(HandlerDescription handlerDescription, Object instance, MessageProducer messageProducer) {
        super(handlerDescription, instance);
        this.messageProducer = messageProducer;
    }

    @Override
    protected String getTypeHeader() {
        return Constants.COMMAND_TYPE;
    }

    @Override
    protected byte[] getValueFromRecord(ConsumerRecord<String, byte[]> record, Parameter parameter) {
        CommandBody commandBody = parameter.getAnnotation(CommandBody.class);
        if (commandBody != null) {
            return record.value();
        }

        CommandHeader commandHeader = parameter.getAnnotation(CommandHeader.class);
        if (commandHeader != null) {
            Headers headers = record.headers();
            Header header = headers.lastHeader(commandHeader.value());
            return header.value();
        }

        throw new RuntimeException("Cannot detect the parameter value!");
    }

    @Override
    protected void handleError(Exception e) {
        LOGGER.error("Error when handle message", e);
        if (e.getCause() instanceof ReplyException replyException) {
            messageProducer.send(
                    replyException.getTopic(),
                    replyException.getKey(),
                    replyException.getValue(),
                    replyException::getHeaders);
        }
    }
}
