package com.enthusiasm.payment.commands;


import com.enthusiasm.events.repository.EventRepository;
import com.enthusiasm.payment.configuration.CommandDispatcher;
import com.enthusiasm.payment.configuration.CommandHandler;
import com.enthusiasm.payment.domain.EMoneyAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CommandDispatcher(service = "payment", aggregate = "emoney-aggregate")
public class EMoneyCommandHandler implements EMoneyCommandService {

    private final EventRepository eventRepository;

    public EMoneyCommandHandler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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
    @CommandHandler(commandType = "WITHDRAW_ACCOUNT_COMMAND")
    public void handle(WithdrawAmountCommand command) {
        final var aggregate = eventRepository.load(command.aggregateId(), EMoneyAggregate.class);
        aggregate.withdraw(command.amount());
        eventRepository.save(aggregate);
    }
}
