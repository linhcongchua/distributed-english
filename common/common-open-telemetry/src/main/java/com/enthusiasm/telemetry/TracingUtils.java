package com.enthusiasm.telemetry;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;

public class TracingUtils {

    private TracingUtils() {
    }

    public static TracingSpan getTracingSpan() {
        Span current = Span.current();
        SpanContext spanContext = current.getSpanContext();
        return new TracingSpan(spanContext.getTraceId(), spanContext.getSpanId());
    }

    public static Span from(TracingSpan tracingSpan, String serviceName, String operation) {
        SpanContext parentContext = SpanContext.createFromRemoteParent(
                tracingSpan.traceId(),
                tracingSpan.spanId(),
                TraceFlags.getSampled(),
                TraceState.getDefault()
        );

        Tracer tracer = GlobalOpenTelemetry.getTracer(serviceName);
        SpanBuilder spanBuilder = tracer.spanBuilder(operation);
        spanBuilder.setParent(Context.current().with(Span.wrap(parentContext)));
        return spanBuilder.startSpan();
    }
}
