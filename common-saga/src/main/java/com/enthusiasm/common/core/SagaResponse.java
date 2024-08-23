package com.enthusiasm.common.core;

public interface SagaResponse {
    default Response success() {
        return new Response(Response.Status.SUCCESS);
    }
    default Response fail() {
        return new Response(Response.Status.FAIL);
    }
}
