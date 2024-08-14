package com.enthusiasm.saga.core;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Getter
public class Endpoint<C extends Command, State> {
    private final String topic;
    private final BiFunction<State, byte[], Boolean> replyHandler;

    private final Map<String, String> headers;

    private final Function<State, String> keyProvider;
    private final Function<State, C> valueProvider;

    public Endpoint(String topic, BiFunction<State, byte[], Boolean> replyHandler, Map<String, String> headers, Function<State, String> keyProvider, Function<State, C> valueProvider) {
        this.topic = topic;
        this.replyHandler = replyHandler;
        this.headers = headers;
        this.keyProvider = keyProvider;
        this.valueProvider = valueProvider;
    }

    public static <C extends Command, State> EndpointBuilder<C, State> builder() {
        return new EndpointBuilder<>();
    }

    public static class EndpointBuilder<C extends Command, State> {
        private String service;
        private String topic;
        private BiFunction<State, byte[], Boolean> replyHandler;

        private Map<String, String> headers = new HashMap<>();

        private Function<State, String> keyProvider;

        private Function<State, C> valueProvider;

        public EndpointBuilder() {
        }

        public EndpointBuilder<C, State> withService(String service) {
            this.service = service;
            return this;
        }

        public EndpointBuilder<C, State> withTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public EndpointBuilder<C, State> withReplyHandler(BiFunction<State, byte[], Boolean> replyHandler) {
            this.replyHandler = replyHandler;
            return this;
        }

        public EndpointBuilder<C, State> withHeader(String key, String value) {
            if (headers.containsKey(key)) {
                throw new RuntimeException("Fail when trying put duplicate header");
            }
            headers.put(key, value);
            return this;
        }

        public EndpointBuilder<C, State> withKeyProvider(Function<State, String> keyProvider) {
            this.keyProvider = keyProvider;
            return this;
        }

        public EndpointBuilder<C, State> withValueProvider(Function<State, C> commandProvider) {
            this.valueProvider = commandProvider;
            return this;
        }

        public Endpoint<C, State> build() {
            return new Endpoint<>(this.service + '-' + this.topic, this.replyHandler, this.headers, this.keyProvider, this.valueProvider);
        }
    }
}
