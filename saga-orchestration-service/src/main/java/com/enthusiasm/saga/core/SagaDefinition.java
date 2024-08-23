package com.enthusiasm.saga.core;

import com.enthusiasm.common.core.Command;
import com.enthusiasm.common.core.SagaResponse;

import java.util.*;
import java.util.function.Function;

public record SagaDefinition<State extends SagaState>(
        String id,
        String topic,
        Class<State> stateClass,
        List<SagaStep<State>> sagaSteps // unmodified list -> thread safety
) {

    public static <State extends SagaState> SagaDefinitionBuilder<State> builder(String topic) {
        return new SagaDefinitionBuilder<>(topic);
    }

    public static class SagaDefinitionBuilder<State extends SagaState> {
        private final String id;
        private final String topic;
        private Class<State> stateClass;

        private final List<SagaStep<State>> sagaSteps = new ArrayList<>();

        public SagaDefinitionBuilder(String topic) {
            this.id = UUID.randomUUID().toString();
            this.topic = topic;
        }

        public SagaDefinitionBuilder<State> withStateClass(Class<State> stateClass) {
            this.stateClass = stateClass;
            return this;
        }

        public StepBuilder<State> step() {
            return new StepBuilder<>(this);
        }

        public SagaDefinition<State> build() {
            return new SagaDefinition<>(id, topic, stateClass, Collections.unmodifiableList(this.sagaSteps));
        }

        void addStep(SagaStep<State> step) {
            this.sagaSteps.add(step);
        }
    }

    public static class StepBuilder<State extends SagaState> {
        private final SagaDefinitionBuilder<State> holder;

        private SagaStep<State> currentStep;

        public StepBuilder(SagaDefinitionBuilder<State> holder) {
            this.holder = holder;
            this.currentStep = new SagaStep<>(UUID.randomUUID());
        }

        public <C extends Command, Reply extends SagaResponse> StepBuilder<State> invoke(Endpoint<C, State, Reply> endpoint) {
            currentStep.setEndpoint(endpoint);
            return this;
        }

        public <C extends Command, Reply extends SagaResponse> StepBuilder<State> withCompensation(Endpoint<C, State, Reply> compensationEndpoint) {
            if (currentStep == null) {
                throw new RuntimeException("Wrong config order");
            }
            currentStep.setCompensation(compensationEndpoint);
            return this;
        }

        public SagaDefinitionBuilder<State> next() {
            this.holder.addStep(currentStep);
            return this.holder;
        }
    }

}
