package com.enthusiasm.common.core;

public record Response(
        Status status
) {
    public enum Status {
        SUCCESS,
        FAIL
    }
}
