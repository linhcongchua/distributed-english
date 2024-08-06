package com.enthusiasm.payment.commands;

import java.math.BigDecimal;

public record WithdrawAmountCommand (String aggregateId, BigDecimal amount) {}