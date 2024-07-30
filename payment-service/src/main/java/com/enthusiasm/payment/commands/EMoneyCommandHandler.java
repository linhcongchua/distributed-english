package com.enthusiasm.payment.commands;


import com.enthusiasm.events.repository.EventRepository;
import com.enthusiasm.payment.domain.EMoneyAggregate;

public class EMoneyCommandHandler implements EMoneyCommandService {

    private final EventRepository eventRepository;

    public EMoneyCommandHandler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void handle(CreateAccountCommand command) {
        final var aggregate = new EMoneyAggregate(command.aggregateID());
        aggregate.createAccount();
        eventRepository.save(aggregate);
    }

    @Override
    public void handle(DepositAmountCommand command) {
        final var aggregate = eventRepository.load(command.aggregateId(), EMoneyAggregate.class);
        aggregate.deposit(command.amount());
        eventRepository.save(aggregate);
    }

    @Override
    public void handle(WithdrawAmountCommand command) {
        final var aggregate = eventRepository.load(command.aggregateId(), EMoneyAggregate.class);
        aggregate.withdraw(command.amount());
        eventRepository.save(aggregate);
    }
}
