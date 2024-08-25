package com.enthusiasm.saga.core;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SagaStep<State> {
    private final UUID stepId;

    private Endpoint<?, State, ?> endpoint;
    private Endpoint<?, State, ?> compensation;

    public SagaStep(UUID stepId) {
        this.stepId = stepId;
    }

    public SagaStep(
            UUID stepId,
            Endpoint<?, State, ?> endpoint,
            Endpoint<?, State, ?> compensation) {
        this.stepId = stepId;
    }
}
