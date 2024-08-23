package com.enthusiasm.payment.commands;

import com.enthusiasm.common.core.SagaHeader;

public interface EMoneyCommandService {
    void handle(CreateAccountCommand command);
    void handle(DepositAmountCommand command);
    void handle(WithdrawAmountCommand command);
    void handle(HoldAmountCommand command, SagaHeader sagaHeader);
}
