package com.enthusiasm.saga.core;

import java.util.Optional;
import java.util.UUID;

public class SubSagaStep {
    protected final UUID subStepId;

    protected Endpoint endpoint;
    protected Optional<Endpoint> compensation;

    public SubSagaStep(UUID subStepId) {
        this.subStepId = subStepId;
    }
}
