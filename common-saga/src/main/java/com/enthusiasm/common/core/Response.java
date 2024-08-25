package com.enthusiasm.common.core;

public record Response(
        Status status
) implements SagaResponse {
    public enum Status {
        SUCCESS,
        FAIL
    }
}
