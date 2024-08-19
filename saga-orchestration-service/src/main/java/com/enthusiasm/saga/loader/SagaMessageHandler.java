package com.enthusiasm.saga.loader;

import com.enthusiasm.common.jackson.DeserializerUtils;
import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.consumer.MessageHandler;
import com.enthusiasm.producer.MessageProducer;
import com.enthusiasm.saga.core.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public class SagaMessageHandler<State extends SagaState> implements MessageHandler {
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
        try {
            Headers headers = record.headers();

            SagaHeader sagaHeader = getHeader(headers, "SAGA_HEADER", SagaHeader.class);

            // should we lock instance

            byte[] messageValue = record.value();

            if (sagaHeader.isInitial()) {
                State instance = sagaDefinition.initializedFunction().apply(messageValue);
                sagaInstanceRepository.saveInstance(instance, instance.getId());
                triggerStep(0, instance);
                return;
            }


            State instance = sagaInstanceRepository.getInstance(sagaHeader.getInstanceId(), sagaDefinition.stateClass());

            int indexStep = getIndexStep(sagaDefinition.sagaSteps(), sagaHeader.getStepId());

            if (indexStep == -1) {
                throw new RuntimeException("Cannot find step by step id: " + sagaHeader.getStepId());
            }

            SagaStep<State> currentStep = sagaDefinition.sagaSteps().get(indexStep);
            switch (sagaHeader.getFlow()) {
                case NORMAL -> {
                    // handle reply
                    Endpoint<?, State> endpoint = currentStep.getEndpoint();
                    BiFunction<State, byte[], Boolean> replyHandler = endpoint.getReplyHandler();
                    Boolean status = replyHandler.apply(instance, messageValue);

                    if (status) { // send message trigger next step
                        int indexNextStep = indexStep + 1;
                        triggerStep(indexNextStep, instance);
                    } else { // trigger compensation rollback
                        triggerCompensation(indexStep, instance);
                    }
                }
                case COMPENSATION -> {
                    Optional<Endpoint<?, State>> compensation = currentStep.getCompensation();
                    if (compensation.isEmpty()) {
                        LOGGER.warn("Compensation is empty!");
                        break;
                    }
                    Endpoint<?, State> compensationEndpoint = compensation.get();
                    BiFunction<State, byte[], Boolean> replyHandler = compensationEndpoint.getReplyHandler();
                    Boolean status = replyHandler.apply(instance, messageValue);
                    if (!status) {
                        LOGGER.warn("Fail to handle compensation!");
                    }

                    // send message trigger next compensation
                    triggerCompensation(indexStep, instance);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error handle handle message", e);
        }
    }

    private void triggerStep(int currentIndex, State instance) {
        if (currentIndex >= sagaDefinition.sagaSteps().size()) {
            return;
        }

        SagaStep<State> nextStep = sagaDefinition.sagaSteps().get(currentIndex);
        Endpoint<?, State> nextEndpoint = nextStep.getEndpoint();
        messageProducer.send(
                nextEndpoint.getTopic(),
                nextEndpoint.getKeyProvider().apply(instance),
                SerializerUtils.serializeToJsonBytes(nextEndpoint.getValueProvider().apply(instance)),
                nextEndpoint.getHeaders() // todo: add header
        );
    }

    private void triggerCompensation(int currentIndex, State instance) {
        int indexNextStep = currentIndex - 1;
        while (indexNextStep >= 0 && sagaDefinition.sagaSteps().get(indexNextStep).getCompensation().isEmpty()) {
            indexNextStep--;
        }
        if (indexNextStep < 0) {
            return;
        }
        SagaStep<State> nextStep = sagaDefinition.sagaSteps().get(indexNextStep);
        Endpoint<?, State> nextStepCompensation = nextStep.getCompensation().get();
        messageProducer.send(
                nextStepCompensation.getTopic(),
                nextStepCompensation.getKeyProvider().apply(instance),
                SerializerUtils.serializeToJsonBytes(nextStepCompensation.getValueProvider().apply(instance)),
                nextStepCompensation.getHeaders()
        );
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

    private static <T> T getHeader(Headers headers, String key, Class<T> clazz) {
        Header header = headers.lastHeader(key);
        byte[] valueHeader = header.value();
        if (valueHeader == null || valueHeader.length == 0) {
            throw new RuntimeException("Not found step header");
        }
        return DeserializerUtils.deserialize(valueHeader, clazz);
    }
}
