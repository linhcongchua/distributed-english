package com.enthusiasm.payment.domain;

import com.enthusiasm.events.AggregateRoot;
import com.enthusiasm.events.Event;
import com.enthusiasm.events.SerializerUtils;
import com.enthusiasm.payment.events.BalanceDepositedEvent;
import com.enthusiasm.payment.events.BalanceWithdrewEvent;
import com.enthusiasm.payment.events.AccountCreatedEvent;

import java.math.BigDecimal;
import java.util.Objects;

public class EMoneyAggregate extends AggregateRoot {

    public static final String AGGREGATE_TYPE = "MoneyAggregate";

    private BigDecimal balance;

    public EMoneyAggregate(String aggregateId) {
        super(aggregateId, AGGREGATE_TYPE);
        this.balance = BigDecimal.valueOf(0);
    }

    @Override
    public void when(Event event) {
        var eventType = MoneyEventType.valueOf(event.getEventType());
        switch (eventType) {
            case ACCOUNT_INITIALIZED -> {
                AccountCreatedEvent e = new AccountCreatedEvent();
                handle(e);
            }
            case BALANCE_WITHDREW -> {
                BalanceWithdrewEvent e = SerializerUtils.deserializeFromJsonBytes(event.getData(), BalanceWithdrewEvent.class);
                handle(e);
            }
            case BALANCE_DEPOSITED -> {
                BalanceDepositedEvent e = SerializerUtils.deserializeFromJsonBytes(event.getData(), BalanceDepositedEvent.class);
                handle(e);
            }
        }
    }

    private void handle(final AccountCreatedEvent event) {
        this.balance = BigDecimal.valueOf(0);
    }

    private void handle(final BalanceDepositedEvent event) {
        Objects.requireNonNull(event.getAmount());
        this.balance = this.balance.add(event.getAmount());
    }

    private void handle(final BalanceWithdrewEvent event) {
        Objects.requireNonNull(event.getAmount());
        if (this.balance.compareTo(event.getAmount()) < 0) {
            throw new RuntimeException("Balance not enough to withdraw");
        }
        this.balance = this.balance.subtract(event.getAmount());
    }

    public void deposit(BigDecimal amount) {
        final var data = BalanceDepositedEvent.builder()
                .aggregateId(this.getId())
                .amount(amount)
                .build();
        final byte[] dataBytes = SerializerUtils.serializeToJsonBytes(data);
        final var event = this.createEvent(MoneyEventType.BALANCE_DEPOSITED.name(), dataBytes, null);
        apply(event);
    }

    public void withdraw(BigDecimal amount) {
    }

    enum MoneyEventType {
        ACCOUNT_INITIALIZED,
        BALANCE_DEPOSITED,
        BALANCE_WITHDREW
    }


    public void createAccount() {
        final var data = AccountCreatedEvent.builder().build();

        final byte[] data
    }
}
