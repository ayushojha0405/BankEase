package com.bankease.account.dto;

import com.bankease.account.entity.Account;
import com.bankease.account.entity.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Account details response")
public class AccountResponse {

    @Schema(description = "Unique account ID", example = "1")
    private Long accountId;

    @Schema(description = "Account holder full name", example = "Rahul Sharma")
    private String accountHolderName;

    @Schema(description = "Account type", example = "SAVINGS")
    private AccountType accountType;

    @Schema(description = "Current account balance", example = "5000.00")
    private BigDecimal balance;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public AccountResponse() {
    }

    public AccountResponse(Long accountId, String accountHolderName, AccountType accountType, BigDecimal balance, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
                account.getAccountId(),
                account.getAccountHolderName(),
                account.getAccountType(),
                account.getBalance(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    public static AccountResponseBuilder builder() {
        return new AccountResponseBuilder();
    }

    public static class AccountResponseBuilder {
        private Long accountId;
        private String accountHolderName;
        private AccountType accountType;
        private BigDecimal balance;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public AccountResponseBuilder accountId(Long accountId) {
            this.accountId = accountId;
            return this;
        }

        public AccountResponseBuilder accountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
            return this;
        }

        public AccountResponseBuilder accountType(AccountType accountType) {
            this.accountType = accountType;
            return this;
        }

        public AccountResponseBuilder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public AccountResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AccountResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AccountResponse build() {
            return new AccountResponse(accountId, accountHolderName, accountType, balance, createdAt, updatedAt);
        }
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

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
