package com.enthusiasm.common.core;

public interface SuccessResponse {
    default Response response() {
        return new Response(Response.Status.SUCCESS);
    }
}
