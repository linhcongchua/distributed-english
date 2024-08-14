package com.enthusiasm.saga.core;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class SagaStep<State> {
    private final UUID stepId;
    String description;

    List<SubSagaStep<?, State>> subSagaSteps;

    public SagaStep(UUID stepId) {
        this.stepId = stepId;
    }

    public SagaStep(UUID stepId, String description, List<SubSagaStep<?, State>> subSagaSteps) {
        this.stepId = stepId;
        this.description = description;
        this.subSagaSteps = subSagaSteps;
    }
}
