package com.enthusiam.gateway.common;

public class SagaHeader {
    private boolean isInitial;
    private String stepId;
    private String instanceId;
    private SagaFlow flow;

    public enum SagaFlow {
        NORMAL,
        COMPENSATION
    }

    public SagaHeader() {
    }

    public SagaHeader(boolean isInitial, String stepId, String instanceId, SagaFlow flow) {
        this.isInitial = isInitial;
        this.stepId = stepId;
        this.instanceId = instanceId;
        this.flow = flow;
    }

    public static SagaHeader getInitial() {
        SagaHeader sagaHeader = new SagaHeader();
        sagaHeader.setInitial(true);
        return sagaHeader;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public SagaFlow getFlow() {
        return flow;
    }

    public void setFlow(SagaFlow flow) {
        this.flow = flow;
    }
}
