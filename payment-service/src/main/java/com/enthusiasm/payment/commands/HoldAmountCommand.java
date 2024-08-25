package com.enthusiasm.payment.commands;

import java.math.BigDecimal;

public record HoldAmountCommand (String userId, BigDecimal amount) {
}
