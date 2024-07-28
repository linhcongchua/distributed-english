package com.enthusiasm.consumer;

public interface MessageConsumer {
    MessageSubscription subscribe(String topic);
}
