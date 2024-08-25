package com.enthusiasm.dispatcher.command;

import lombok.Getter;
import org.apache.kafka.common.header.Header;

import java.util.List;

@Getter
public class ReplyException extends RuntimeException{
    private final String topic;
    private final String key;
    private final byte[] value;
    private final List<Header> headers;

    public ReplyException(String topic, String key, byte[] value, List<Header> headers) {
        this.topic = topic;
        this.key = key;
        this.value = value;
        this.headers = headers;
    }
}
