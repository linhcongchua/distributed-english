package com.enthusiasm.saga.core;

import lombok.Data;

import java.util.Optional;
import java.util.UUID;

@Data
public class SubSagaStep<C extends Command, State> { // todo: C type parameter
    private final UUID subStepId;

    private Endpoint<?, State> endpoint;
    private Optional<Endpoint<?, State>> compensation;

    public SubSagaStep(UUID subStepId) {
        this.subStepId = subStepId;
    }
}
