package com.bankease.transaction.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDto {
    private Long accountId;
    private String accountHolderName;
    private String accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public AccountDto() {
    }

    public AccountDto(Long accountId, String accountHolderName, String accountType, BigDecimal balance, LocalDateTime createdAt) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt;
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
