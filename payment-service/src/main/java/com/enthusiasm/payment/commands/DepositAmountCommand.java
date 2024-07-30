package com.enthusiasm.payment.commands;

import java.math.BigDecimal;

public record DepositAmountCommand (String aggregateId, BigDecimal amount) {}
