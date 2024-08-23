package com.enthusiasm.payment.commands;


import com.enthusiasm.common.core.SagaHeader;
import com.enthusiasm.dispatcher.command.CommandBody;
import com.enthusiasm.dispatcher.command.CommandDispatcher;
import com.enthusiasm.dispatcher.command.CommandHandler;
import com.enthusiasm.dispatcher.command.CommandHeader;
import com.enthusiasm.events.repository.EventRepository;
import com.enthusiasm.outbox.EventDispatcher;
import com.enthusiasm.payment.domain.EMoneyAggregate;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

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
    public void handle(@CommandBody WithdrawAmountCommand command, @CommandHeader("SAGA_HEADER") SagaHeader sagaHeader) {
        final var aggregate = eventRepository.load(command.aggregateId(), EMoneyAggregate.class);
        aggregate.withdraw(command.amount());
        eventRepository.save(aggregate);


    }
}
