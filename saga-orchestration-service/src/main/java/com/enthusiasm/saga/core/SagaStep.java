package com.enthusiasm.saga.core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SagaStep {
    private final UUID stepId;
    String description;

    List<SubSagaStep> subSagaSteps;

    public SagaStep(UUID stepId) {
        this.stepId = stepId;
    }

    public SagaStep(UUID stepId, String description, List<SubSagaStep> subSagaSteps) {
        this.stepId = stepId;
        this.description = description;
        this.subSagaSteps = subSagaSteps;
    }
}
