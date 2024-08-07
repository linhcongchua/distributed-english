package com.enthusiasm.payment.events;

import com.enthusiasm.events.BaseEvent;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper=false)
public class BalanceDepositedEvent extends BaseEvent {
    private final BigDecimal amount;

    @Builder
    public BalanceDepositedEvent(String aggregateId, BigDecimal amount) {
        super(aggregateId);
        this.amount = amount;
    }
}
