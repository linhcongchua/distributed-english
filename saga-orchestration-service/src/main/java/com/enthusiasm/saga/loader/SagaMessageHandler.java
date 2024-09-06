package com.enthusiasm.saga.loader;

import com.enthusiasm.common.core.Command;
import com.enthusiasm.common.core.SagaFlow;
import com.enthusiasm.common.core.SagaHeader;
import com.enthusiasm.common.core.SagaResponse;
import com.enthusiasm.common.jackson.DeserializerUtils;
import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.consumer.MessageHandler;
import com.enthusiasm.producer.MessageProducer;
import com.enthusiasm.saga.core.Endpoint;
import com.enthusiasm.saga.core.SagaDefinition;
import com.enthusiasm.saga.core.SagaState;
import com.enthusiasm.saga.core.SagaStep;
import com.enthusiasm.saga.utils.RecordHeaderUtils;
import com.enthusiasm.telemetry.TracingSpan;
import com.enthusiasm.telemetry.TracingUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;

public class SagaMessageHandler<State extends SagaState, Reply extends SagaResponse> implements MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SagaMessageHandler.class);

    private final SagaDefinition<State> sagaDefinition;

    private final SagaInstanceRepository sagaInstanceRepository;

    private final MessageProducer messageProducer;

    public SagaMessageHandler(SagaDefinition<State> sagaDefinition, SagaInstanceRepository sagaInstanceRepository, MessageProducer messageProducer) {
        this.sagaDefinition = sagaDefinition;
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.messageProducer = messageProducer;
    }

    @Override
    public void accept(ConsumerRecord<String, byte[]> record) { // todo: lock-using semantic lock???, create saga???
        Span tracing = null;
        try {
            TracingSpan tracingInfo = RecordHeaderUtils.getHeader(record, "TRACING", TracingSpan.class);
            if (tracingInfo != null) {
                tracing = TracingUtils.from(tracingInfo, "saga-service", "handle-message");
                try(Scope ignored = tracing.makeCurrent()) {
                    doFlow(record);
                }
                return;
            }

            doFlow(record);

        } catch (Exception e) {
            LOGGER.error("Error handle handle message", e);
        } finally {
            if (tracing != null) {
                tracing.end();
            }
        }
    }

    private void doFlow(ConsumerRecord<String, byte[]> record) {
        // should we lock instance
        byte[] messageValue = record.value();

        SagaHeader sagaHeader = RecordHeaderUtils.getHeader(record, "EXTRA_HEADER", "SAGA_HEADER", SagaHeader.class);
        if (sagaHeader == null) {
            throw new RuntimeException("Cannot found saga header!");
        }
        if (sagaHeader.isInitial()) { // todo: nullable
            State instance = DeserializerUtils.deserialize(messageValue, sagaDefinition.stateClass());
            sagaInstanceRepository.saveInstance(instance, instance.getId());
            triggerStep(0, instance);
            return;
        }


        State instance = sagaInstanceRepository.getInstance(sagaHeader.instanceId(), sagaDefinition.stateClass());

        int indexStep = getIndexStep(sagaDefinition.sagaSteps(), sagaHeader.stepId());

        if (indexStep == -1) { // todo: magic number
            throw new RuntimeException("Cannot find step by step id: " + sagaHeader.stepId());
        }

        SagaStep<State> currentStep = sagaDefinition.sagaSteps().get(indexStep);
        switch (sagaHeader.flow()) {
            case NORMAL -> {
                // handle reply
                Endpoint<?, State, Reply> endpoint = (Endpoint<?, State, Reply>) currentStep.getEndpoint();
                BiFunction<State, Reply, Boolean> replyHandler = endpoint.getReplyHandler();
                Reply replyValue = DeserializerUtils.deserialize(messageValue, endpoint.getReplyClass());
                Boolean status = replyHandler.apply(instance, replyValue); // handle response here

                if (status) { // send message trigger next step
                    int indexNextStep = indexStep + 1;
                    triggerStep(indexNextStep, instance);
                } else { // trigger compensation rollback
                    triggerCompensation(indexStep, instance);
                }
            }
            case COMPENSATION -> {

                Endpoint<?, State, ?> compensation = currentStep.getCompensation();
                if (compensation == null) {
                    LOGGER.warn("Compensation is empty!");
                    break;
                }
                Endpoint<?, State, Reply> compensationEndpoint = (Endpoint<?, State, Reply>) compensation;
                BiFunction<State, Reply, Boolean> replyHandler = compensationEndpoint.getReplyHandler();
                Reply replyValue = DeserializerUtils.deserialize(messageValue, compensationEndpoint.getReplyClass());
                Boolean status = replyHandler.apply(instance, replyValue);
                if (!status) {
                    LOGGER.warn("Fail to handle compensation!");
                }

                // send message trigger next compensation
                triggerCompensation(indexStep, instance);
            }
        }
    }

    private void triggerStep(int currentIndex, State instance) {
        if (currentIndex >= sagaDefinition.sagaSteps().size()) {
            return;
        }

        SagaStep<State> nextStep = sagaDefinition.sagaSteps().get(currentIndex);
        Endpoint<?, State, ?> nextEndpoint = nextStep.getEndpoint();
        trigger(nextStep, nextEndpoint, instance, SagaFlow.NORMAL);
    }

    private void triggerCompensation(int currentIndex, State instance) {
        int indexNextStep = currentIndex - 1;
        while (indexNextStep >= 0 && sagaDefinition.sagaSteps().get(indexNextStep).getCompensation() == null) {
            indexNextStep--;
        }
        if (indexNextStep < 0) {
            return;
        }
        SagaStep<State> nextStep = sagaDefinition.sagaSteps().get(indexNextStep);
        Endpoint<?, State, ?> nextStepCompensation = nextStep.getCompensation();
        trigger(nextStep, nextStepCompensation, instance, SagaFlow.COMPENSATION);
    }

    private void trigger(SagaStep<State> nextStep, Endpoint<?, State, ?> endpoint, State instance, SagaFlow flow) {

        String key = endpoint.getKeyProvider().apply(instance);
        Command command = endpoint.getValueProvider().apply(instance);
        byte[] value = SerializerUtils.serializeToJsonBytes(command);

        messageProducer.send(
                endpoint.getTopic(),
                key,
                value,
                () -> {
                    Map<String, Object> extraHeader = new HashMap<>(endpoint.getHeaders());
                    SagaHeader sagaHeader = getSagaHeader(nextStep, instance, flow);
                    extraHeader.put("SAGA_HEADER", sagaHeader);
                    RecordHeader header = new RecordHeader("EXTRA_HEADER", SerializerUtils.serializeToJsonBytes(extraHeader));
                    return List.of(header);
                }
        );
    }

    private SagaHeader getSagaHeader(SagaStep<State> nextStep, State instance, SagaFlow flow) {
        return SagaHeader.builder()
                .topic(sagaDefinition.topic())
                .stepId(nextStep.getStepId().toString())
                .instanceId(instance.getId())
                .flow(flow)
                .build();
    }

    private int getIndexStep(List<SagaStep<State>> sagaSteps, String stepId) { // todo: should using cache here
        for (int i = 0; i < sagaSteps.size(); i++) {
            SagaStep<State> sagaStep = sagaSteps.get(i);
            if (Objects.equals(stepId, sagaStep.getStepId().toString())) {
                return i;
            }
        }
        return -1;
    }
}
