package com.enthusiasm.saga.core;

import java.util.List;
import java.util.Optional;

public class SagaStep {
    String description;

    Optional<String> replyTo;
    List<SubSagaStep> subSagaSteps;

    public SagaStep() {
    }

    public SagaStep(String description, String replyTo, List<SubSagaStep> subSagaSteps) {
        this.description = description;
        this.replyTo = Optional.ofNullable(replyTo);
        this.subSagaSteps = subSagaSteps;
    }
}
