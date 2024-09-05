package com.enthusiasm.forum.queries;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(NotFoundException.class)
    public Status handleNotFoundException(NotFoundException exception) {
        return Status.NOT_FOUND.withDescription(exception.getMessage()).withCause(exception);
    }
}
