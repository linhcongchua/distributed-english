package com.enthusiasm.saga.core;

import org.springframework.util.CollectionUtils;

import java.util.*;

public class SagaDefinition {
    private String description;

    private final String id;
    private String topic;
    private List<SagaStep> sagaSteps;

    public SagaDefinition(String id, String topic, String description, List<SagaStep> sagaSteps) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.sagaSteps = sagaSteps;
    }

    public String getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getDescription() {
        return description;
    }

    public List<SagaStep> getSagaSteps() {
        return sagaSteps;
    }

    public static <State> SagaDefinitionBuilder<State> builder(String topic) {
        return new SagaDefinitionBuilder<>(topic);
    }

    public static class SagaDefinitionBuilder<State> {
        private final String id;

        private final String topic;

        private String description;

        private List<SagaStep> sagaSteps = new ArrayList<>();

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

        public SagaDefinition build() {
            return new SagaDefinition(id, topic, this.description, Collections.unmodifiableList(this.sagaSteps));
        }

        void addStep(SagaStep step) {
            this.sagaSteps.add(step);
        }
    }

    public static class StepBuilder<State> {
        private final SagaDefinitionBuilder<State> holder;

        private List<SubSagaStep> subSagaSteps = new ArrayList<>();
        private String description;

        private SubSagaStep currentSubStep;

        public StepBuilder(SagaDefinitionBuilder<State> holder) {
            this.holder = holder;
        }

        public StepBuilder<State> withDescription(String description) {
            this.description = description;
            return this;
        }

        public <C extends Command> StepBuilder<State> invoke(Endpoint<C, State> endpoint) {
            currentSubStep = new SubSagaStep(UUID.randomUUID());
            currentSubStep.endpoint = endpoint;
            return this;
        }

        public <C extends Command> StepBuilder<State> withCompensation(Endpoint<C, State> compensationEndpoint) {
            if (currentSubStep == null) {
                throw new RuntimeException("Wrong config order");
            }
            currentSubStep.compensation = Optional.of(compensationEndpoint);
            return this;
        }

        public SagaDefinitionBuilder<State> next() {
            if (CollectionUtils.isEmpty(subSagaSteps)) {
                throw new  RuntimeException("Sub step cannot be empty");
            }
            var step = new SagaStep(
                    UUID.randomUUID(),
                    this.description,
                    Collections.unmodifiableList(subSagaSteps)
            );

            this.holder.addStep(step);
            return this.holder;
        }
    }

}
