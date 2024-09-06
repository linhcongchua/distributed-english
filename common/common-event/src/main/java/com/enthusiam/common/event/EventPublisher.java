package com.enthusiam.common.event;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

public interface EventPublisher {
    default <T> void publishEvent(String key, String topic, String eventType, T payload) {
        Span current = Span.current();
        SpanContext spanContext = current.getSpanContext();

        EventOutbox<T> eventWrapper = EventOutbox.<T>builder()
                .key(key)
                .topic(topic)
                .payload(payload)
                .eventType(eventType)
                .spanContext(spanContext)
                .build();

        publish(eventWrapper);
    }

    <T> void publish(EventOutbox<T> eventWrapper);
}
