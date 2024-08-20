package com.enthusiasm.saga.core;

import com.enthusiasm.common.core.Command;

import java.util.*;
import java.util.function.Function;

public record SagaDefinition<State extends SagaState>(
        String id,
        String topic,
        String description,
        Class<State> stateClass,
        Function<byte[], State> initializedFunction,
        List<SagaStep<State>> sagaSteps // unmodified list -> thread safety
) {

    public static <State extends SagaState> SagaDefinitionBuilder<State> builder(String topic) {
        return new SagaDefinitionBuilder<>(topic);
    }

    public static class SagaDefinitionBuilder<State extends SagaState> {
        private final String id;

        private final String topic;

        private String description;
        private Class<State> stateClass;

        private Function<byte[], State> initializedFunction;

        private final List<SagaStep<State>> sagaSteps = new ArrayList<>();

        public SagaDefinitionBuilder(String topic) {
            this.id = UUID.randomUUID().toString();
            this.topic = topic;
        }

        public SagaDefinitionBuilder<State> withDescription(String description) {
            this.description = description;
            return this;
        }

        public SagaDefinitionBuilder<State> withStateClass(Class<State> stateClass) {
            this.stateClass = stateClass;
            return this;
        }

        public SagaDefinitionBuilder<State> withInitializedFunction(Function<byte[], State> initializedFunction) {
            this.initializedFunction = initializedFunction;
            return this;
        }

        public StepBuilder<State> step() {
            return new StepBuilder<>(this);
        }

        public SagaDefinition<State> build() {
            return new SagaDefinition<>(id, topic, this.description, stateClass, this.initializedFunction, Collections.unmodifiableList(this.sagaSteps));
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

        public StepBuilder<State> withDescription(String description) {
            this.currentStep.setDescription(description);
            return this;
        }

        public <C extends Command> StepBuilder<State> invoke(Endpoint<C, State> endpoint) {
            currentStep.setEndpoint(endpoint);
            return this;
        }

        public <C extends Command> StepBuilder<State> withCompensation(Endpoint<C, State> compensationEndpoint) {
            if (currentStep == null) {
                throw new RuntimeException("Wrong config order");
            }
            currentStep.setCompensation(Optional.of(compensationEndpoint));
            return this;
        }

        public SagaDefinitionBuilder<State> next() {
            this.holder.addStep(currentStep);
            return this.holder;
        }
    }

}
