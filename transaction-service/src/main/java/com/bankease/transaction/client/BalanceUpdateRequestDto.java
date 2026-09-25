package com.bankease.transaction.client;

import java.math.BigDecimal;

public class BalanceUpdateRequestDto {
    private BalanceOperation operation;
    private BigDecimal amount;
    private String reference;

    public BalanceUpdateRequestDto() {
    }

    public BalanceUpdateRequestDto(BalanceOperation operation, BigDecimal amount, String reference) {
        this.operation = operation;
        this.amount = amount;
        this.reference = reference;
    }

    public BalanceOperation getOperation() {
        return operation;
    }

    public void setOperation(BalanceOperation operation) {
        this.operation = operation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
