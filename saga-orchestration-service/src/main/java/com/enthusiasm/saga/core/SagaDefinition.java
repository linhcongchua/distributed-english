package com.enthusiasm.saga.core;

import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Getter
public record SagaDefinition<State>(
        String id,
        String topic,
        String description,
        List<SagaStep<State>> sagaSteps) {

    public static <State> SagaDefinitionBuilder<State> builder(String topic) {
        return new SagaDefinitionBuilder<>(topic);
    }

    public static class SagaDefinitionBuilder<State> {
        private final String id;

        private final String topic;

        private String description;

        private final List<SagaStep<State>> sagaSteps = new ArrayList<>();

        public SagaDefinitionBuilder(String topic) {
            this.id = UUID.randomUUID().toString();
            this.topic = topic;
        }

        public SagaDefinitionBuilder<State> withDescription(String description) {
            this.description = description;
            return this;
        }

        public StepBuilder<State> step() {
            return new StepBuilder<State>(this);
        }

        public SagaDefinition<State> build() {
            return new SagaDefinition<>(id, topic, this.description, Collections.unmodifiableList(this.sagaSteps));
        }

        void addStep(SagaStep<State> step) {
            this.sagaSteps.add(step);
        }
    }

    public static class StepBuilder<State> {
        private final SagaDefinitionBuilder<State> holder;

        private final List<SubSagaStep<?, State>> subSagaSteps = new ArrayList<>();
        private String description;

        private SubSagaStep<?, State> currentSubStep;

        public StepBuilder(SagaDefinitionBuilder<State> holder) {
            this.holder = holder;
        }

        public StepBuilder<State> withDescription(String description) {
            this.description = description;
            return this;
        }

        public <C extends Command> StepBuilder<State> invoke(Endpoint<C, State> endpoint) {
            currentSubStep = new SubSagaStep<C, State>(UUID.randomUUID());
            currentSubStep.setEndpoint(endpoint);
            return this;
        }

        public <C extends Command> StepBuilder<State> withCompensation(Endpoint<C, State> compensationEndpoint) {
            if (currentSubStep == null) {
                throw new RuntimeException("Wrong config order");
            }
            currentSubStep.setCompensation(Optional.of(compensationEndpoint));
            return this;
        }

        public SagaDefinitionBuilder<State> next() {
            if (CollectionUtils.isEmpty(subSagaSteps)) {
                throw new RuntimeException("Sub step cannot be empty");
            }
            var step = new SagaStep<>(
                    UUID.randomUUID(),
                    this.description,
                    Collections.unmodifiableList(subSagaSteps)
            );

            this.holder.addStep(step);
            return this.holder;
        }
    }

}
