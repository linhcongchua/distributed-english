package com.enthusiasm.payment.commands;


import com.enthusiasm.common.core.SagaHeader;
import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.common.payment.response.HoldAmountResponse;
import com.enthusiasm.dispatcher.command.*;
import com.enthusiasm.events.repository.EventRepository;
import com.enthusiasm.outbox.EventDispatcher;
import com.enthusiasm.payment.domain.EMoneyAggregate;
import jakarta.transaction.Transactional;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@CommandDispatcher(service = "payment-service", topic = "emoney")
public class EMoneyCommandHandler implements EMoneyCommandService {

    private final EventRepository eventRepository;

    private final EventDispatcher eventDispatcher;

    public EMoneyCommandHandler(EventRepository eventRepository, EventDispatcher eventDispatcher) {
        this.eventRepository = eventRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    @CommandHandler(commandType = "CREATE_ACCOUNT_COMMAND")
    public void handle(CreateAccountCommand command) {
        final var aggregate = new EMoneyAggregate(command.aggregateID());
        aggregate.createAccount();
        eventRepository.save(aggregate);
    }

    @Override
    @CommandHandler(commandType = "DEPOSIT_ACCOUNT_COMMAND")
    public void handle(DepositAmountCommand command) {
        final var aggregate = eventRepository.load(command.aggregateId(), EMoneyAggregate.class);
        aggregate.deposit(command.amount());
        eventRepository.save(aggregate);
    }

    @Override
    @Transactional
    @CommandHandler(commandType = "WITHDRAW_ACCOUNT_COMMAND")
    public void handle(@CommandBody WithdrawAmountCommand command) {
        final var aggregate = eventRepository.load(command.aggregateId(), EMoneyAggregate.class);
        aggregate.withdraw(command.amount());
        eventRepository.save(aggregate);
    }

    @Override
    @Transactional
    @CommandHandler(commandType = "HOLD_ACCOUNT_COMMAND")
    public void handle(HoldAmountCommand command, SagaHeader sagaHeader) {
        try { // todo: should I use AOP for remove try-catch block
            final var aggregate = eventRepository.load(command.userId(), EMoneyAggregate.class);
            aggregate.withdraw(command.amount());
            eventRepository.save(aggregate);

            HoldAmountResponse response = new HoldAmountResponse(
                    command.userId(),
                    sagaHeader.topic(),
                    new String(SerializerUtils.serializeToJsonBytes(sagaHeader), StandardCharsets.UTF_8)
            );
            eventDispatcher.onExportedEvent(response);
        } catch (Exception exception) {
            RecordHeader header = new RecordHeader("SAGA_HEADER", SerializerUtils.serializeToJsonBytes(sagaHeader));
            throw new ReplyException(exception.getMessage(), sagaHeader.topic(), command.userId(), List.of(header));
        }
    }
}
