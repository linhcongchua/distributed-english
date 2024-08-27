package com.enthusiasm.common.core;

public interface SuccessFailHandler {
    default boolean handleSuccessFail(Response response) {
        return response.status() == Response.Status.SUCCESS;
    }
}
