package com.enthusiasm.saga.core;

import com.enthusiasm.common.core.Command;
import com.enthusiasm.common.core.SagaResponse;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Getter
public class Endpoint<C extends Command, State, Reply extends SagaResponse> {
    private final Class<Reply> replyClass;
    private final String topic;
    private final BiFunction<State, Reply, Boolean> replyHandler;

    private final Map<String, String> headers;

    private final Function<State, String> keyProvider;
    private final Function<State, C> valueProvider;

    public Endpoint(Class<Reply> replyClass, String topic, BiFunction<State, Reply, Boolean> replyHandler, Map<String, String> headers, Function<State, String> keyProvider, Function<State, C> valueProvider) {
        this.replyClass = replyClass;
        this.topic = topic;
        this.replyHandler = replyHandler;
        this.headers = headers;
        this.keyProvider = keyProvider;
        this.valueProvider = valueProvider;
    }

    public static <C extends Command, State, Reply extends SagaResponse> EndpointBuilder<C, State, Reply> builder() {
        return new EndpointBuilder<>();
    }

    public static class EndpointBuilder<C extends Command, State, Reply extends SagaResponse> {
        private String service;
        private String topic;
        private BiFunction<State, Reply, Boolean> replyHandler;

        private Map<String, String> headers = new HashMap<>();

        private Function<State, String> keyProvider;

        private Function<State, C> valueProvider;

        public EndpointBuilder() {
        }

        public EndpointBuilder<C, State, Reply> withService(String service) {
            this.service = service;
            return this;
        }

        public EndpointBuilder<C, State, Reply> withTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public EndpointBuilder<C, State, Reply> withReplyHandler(BiFunction<State, Reply, Boolean> replyHandler) {
            this.replyHandler = replyHandler;
            return this;
        }

        public EndpointBuilder<C, State, Reply> withHeader(String key, String value) {
            if (headers.containsKey(key)) {
                throw new RuntimeException("Fail when trying put duplicate header");
            }
            headers.put(key, value);
            return this;
        }

        public EndpointBuilder<C, State, Reply> withKeyProvider(Function<State, String> keyProvider) {
            this.keyProvider = keyProvider;
            return this;
        }

        public EndpointBuilder<C, State, Reply> withValueProvider(Function<State, C> commandProvider) {
            this.valueProvider = commandProvider;
            return this;
        }

        public Endpoint<C, State, Reply> build(Class<Reply> replyClass) {
            return new Endpoint<>(replyClass, this.service + '-' + this.topic, this.replyHandler, this.headers, this.keyProvider, this.valueProvider);
        }
    }
}
