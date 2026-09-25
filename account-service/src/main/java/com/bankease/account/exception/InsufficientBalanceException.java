package com.bankease.account.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long accountId, BigDecimal currentBalance, BigDecimal requestedAmount) {
        super(String.format("Insufficient balance in account ID %d. Current balance: %s, Requested debit: %s",
                accountId, currentBalance.toPlainString(), requestedAmount.toPlainString()));
    }
}
