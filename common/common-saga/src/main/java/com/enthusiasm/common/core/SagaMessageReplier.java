package com.enthusiasm.common.core;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

public interface SagaMessageReplier {
    default <T> void replySaga(String key, SagaHeader sagaHeader, T payload) {
        Span current = Span.current();
        SpanContext spanContext = current.getSpanContext();

        SagaReplyOutbox<T> reply = SagaReplyOutbox.<T>builder()
                .key(key)
                .spanContext(spanContext)
                .sagaHeader(sagaHeader)
                .payload(payload)
                .build();
        pushMessage(reply);
    }

    <T> void pushMessage(SagaReplyOutbox<T> reply);
}
