package com.enthusiasm.payment.events;

import com.enthusiasm.events.BaseEvent;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceWithdrewEvent extends BaseEvent {
    private final BigDecimal amount;

    @Builder
    public BalanceWithdrewEvent(String aggregateId, BigDecimal amount) {
        super(aggregateId);
        this.amount = amount;
    }
}
