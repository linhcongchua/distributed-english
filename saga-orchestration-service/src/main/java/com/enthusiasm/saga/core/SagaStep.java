package com.enthusiasm.saga.core;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class SagaStep<State> {
    private final UUID stepId;
    String description;

    private Endpoint<?, State> endpoint;
    private Optional<Endpoint<?, State>> compensation;

    public SagaStep(UUID stepId) {
        this.stepId = stepId;
    }

    public SagaStep(
            UUID stepId,
            String description,
            Endpoint<?, State> endpoint,
            Optional<Endpoint<?, State>> compensation) {
        this.stepId = stepId;
        this.description = description;
    }
}
