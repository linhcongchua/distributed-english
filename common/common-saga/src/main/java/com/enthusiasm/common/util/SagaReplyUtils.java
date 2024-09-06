package com.enthusiasm.common.util;

import com.enthusiasm.common.core.Response;

public class SagaReplyUtils {
    private SagaReplyUtils() {
    }

    public static Response success() {
        return new Response(Response.Status.SUCCESS);
    }

    public static Response fail() {
        return new Response(Response.Status.FAIL);
    }
}
