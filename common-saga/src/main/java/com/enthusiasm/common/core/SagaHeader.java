package com.enthusiasm.common.core;

import lombok.Builder;

@Builder
public record SagaHeader (
        boolean isInitial,
        String topic,
        String stepId,
        String instanceId,
        SagaFlow flow
) {

    public static SagaHeader getInitial() {
        return new SagaHeader(true, null, null, null, null);
    }
}
