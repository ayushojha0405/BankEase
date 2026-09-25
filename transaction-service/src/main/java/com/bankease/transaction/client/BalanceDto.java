package com.bankease.transaction.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BalanceDto {
    private Long accountId;
    private String accountHolderName;
    private BigDecimal currentBalance;
    private LocalDateTime updatedAt;

    public BalanceDto() {
    }

    public BalanceDto(Long accountId, String accountHolderName, BigDecimal currentBalance, LocalDateTime updatedAt) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.currentBalance = currentBalance;
        this.updatedAt = updatedAt;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
