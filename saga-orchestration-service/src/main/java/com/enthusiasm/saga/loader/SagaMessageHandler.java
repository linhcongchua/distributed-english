package com.enthusiasm.saga.loader;

import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.consumer.MessageHandler;
import com.enthusiasm.producer.MessageProducer;
import com.enthusiasm.saga.core.Endpoint;
import com.enthusiasm.saga.core.SagaDefinition;
import com.enthusiasm.saga.core.SagaStep;
import com.enthusiasm.saga.core.SubSagaStep;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SagaMessageHandler<State> implements MessageHandler {
    private final SagaDefinition<State> sagaDefinition;

    private final SagaInstanceRepository sagaInstanceRepository;

    private final MessageProducer messageProducer;

    public SagaMessageHandler(SagaDefinition<State> sagaDefinition, SagaInstanceRepository sagaInstanceRepository, MessageProducer messageProducer) {
        this.sagaDefinition = sagaDefinition;
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.messageProducer = messageProducer;
    }

    @Override
    public void accept(ConsumerRecord<String, byte[]> record) {
        Headers headers = record.headers();

        String sagaDefinitionId = getHeader(headers, "SAGA_DEFINITION_ID");
        String stepId = getHeader(headers, "STEP_ID");
        String instanceId = getHeader(headers, "SAGA_INSTANCE_ID");

        if (sagaDefinition == null) {
            throw new RuntimeException("Cannot find saga definition by id: " + sagaDefinitionId);
        }

        byte[] value = record.value();

        State instance = sagaInstanceRepository.getInstance(instanceId); // todo: ???

        SagaStep<State> step = sagaDefinition.sagaSteps().get(0);
        SubSagaStep<?, State> subSagaStep = step.getSubSagaSteps().get(0);
        Endpoint<?, State> endpoint = subSagaStep.getEndpoint();
        BiFunction<State, byte[], Boolean> replyHandler = endpoint.getReplyHandler();
        replyHandler.apply(instance, value);

        // next step;
        SagaStep<State> nextStep = sagaDefinition.sagaSteps().get(1);
        List<SubSagaStep<?, State>> subSagaSteps = nextStep.getSubSagaSteps();
        for (var subStep : subSagaSteps) { // todo: atomically
            Endpoint<?, State> nextEndpoint = subStep.getEndpoint();
            Function<State, ?> valueProvider = nextEndpoint.getValueProvider();
            messageProducer.send(
                    nextEndpoint.getTopic(),
                    nextEndpoint.getKeyProvider().apply(instance),
                    SerializerUtils.serializeToJsonBytes(valueProvider.apply(instance)),
                    nextEndpoint.getHeaders());
        }
    }

    private static String getHeader(Headers headers, String key) {
        Header header = headers.lastHeader(key);
        if (header == null) {
            throw new RuntimeException("Header with key '" + key + "' not exist.");
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }
}
