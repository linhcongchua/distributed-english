package com.enthusiasm.saga.core;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SagaDefinition {
    private String description;
    private List<SagaStep> sagaSteps;

    public SagaDefinition(String description, List<SagaStep> sagaSteps) {
        this.description = description;
        this.sagaSteps = sagaSteps;
    }

    public String getDescription() {
        return description;
    }

    public List<SagaStep> getSagaSteps() {
        return sagaSteps;
    }

    public static <State> SagaDefinitionBuilder<State> builder() {
        return new SagaDefinitionBuilder<>();
    }

    public static class SagaDefinitionBuilder<State> {
        private String description;
        private List<SagaStep> sagaSteps = new ArrayList<>();

        public SagaDefinitionBuilder<State> withDescription(String description) {
            this.description = description;
            return this;
        }

        public StepBuilder<State> step() {
            return new StepBuilder<State>(this);
        }

        public SagaDefinition build() {
            return new SagaDefinition(this.description, Collections.unmodifiableList(this.sagaSteps));
        }

        void addStep(SagaStep step) {
            this.sagaSteps.add(step);
        }
    }

    public static class StepBuilder<State> {
        private final SagaDefinitionBuilder<State> holder;

        private List<SubSagaStep> subSagaSteps = new ArrayList<>();
        private String description;
        private String replyTo;

        private SubSagaStep currentSubStep;

        public StepBuilder(SagaDefinitionBuilder<State> holder) {
            this.holder = holder;
        }

        public StepBuilder<State> withDescription(String description) {
            this.description = description;
            return this;
        }

        public StepBuilder<State> withReplyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public StepBuilder<State> invoke(Invoke invoke, Consumer<State> stateTransform) {
            currentSubStep = new SubSagaStep();
            currentSubStep.invoke = invoke;
            return this;
        }

        public StepBuilder<State> withCompensation(Compensation compensation, Consumer<State> stateTransform) {
            if (currentSubStep == null) {
                throw new RuntimeException("Wrong config order");
            }
            currentSubStep.compensation = Optional.of(compensation);
            return this;
        }

        public SagaDefinitionBuilder<State> next() {
            if (CollectionUtils.isEmpty(subSagaSteps)) {
                throw new  RuntimeException("Sub step cannot be empty");
            }
            var step = new SagaStep(
                    this.description,
                    this.replyTo,
                    Collections.unmodifiableList(subSagaSteps)
            );

            this.holder.addStep(step);
            return this.holder;
        }
    }

}
