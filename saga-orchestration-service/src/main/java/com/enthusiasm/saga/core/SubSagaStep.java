package com.enthusiasm.saga.core;

import java.util.Optional;

public class SubSagaStep {
    protected Invoke invoke;
    protected Optional<Compensation> compensation;
}
