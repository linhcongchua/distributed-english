package com.enthusiasm.saga.orchestration.command;

import com.enthusiasm.common.core.Command;

import java.math.BigDecimal;
import java.util.UUID;

public class HoldRewardCommand implements Command {
    private UUID userId;
    private BigDecimal amount;

    public HoldRewardCommand(UUID userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public UUID getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
