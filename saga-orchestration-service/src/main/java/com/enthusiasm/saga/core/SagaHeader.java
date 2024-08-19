package com.enthusiasm.saga.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SagaHeader {
    private boolean isInitial;
    private String stepId;
    private String instanceId;
    private SagaFlow flow;
}
