package com.enthusiasm.payment.commands;

public interface EMoneyCommandService {
    void handle(CreateAccountCommand command);
    void handle(DepositAmountCommand command);
    void handle(WithdrawAmountCommand command);
}
