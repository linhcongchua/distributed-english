package com.enthusiasm.telemetry;

public record TracingSpan(
        String traceId,
        String spanId
) {
}
