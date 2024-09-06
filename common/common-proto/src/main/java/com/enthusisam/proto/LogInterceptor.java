package com.enthusisam.proto;

import com.enthusiasm.proto.PostDetailRequest;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInterceptor implements ServerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> next) {
        LOGGER.info("---> Receive gRPC request {}", metadata);
        return next.startCall(serverCall, metadata);
    }
}
