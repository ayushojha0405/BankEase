package com.bankease.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Account balance response")
public class BalanceResponse {

    @Schema(description = "Account ID", example = "1")
    private Long accountId;

    @Schema(description = "Account holder name", example = "Rahul Sharma")
    private String accountHolderName;

    @Schema(description = "Current available balance", example = "4000.00")
    private BigDecimal currentBalance;

    @Schema(description = "Last timestamp when balance was updated")
    private LocalDateTime updatedAt;

    public BalanceResponse() {
    }

    public BalanceResponse(Long accountId, String accountHolderName, BigDecimal currentBalance, LocalDateTime updatedAt) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.currentBalance = currentBalance;
        this.updatedAt = updatedAt;
    }

    public static BalanceResponseBuilder builder() {
        return new BalanceResponseBuilder();
    }

    public static class BalanceResponseBuilder {
        private Long accountId;
        private String accountHolderName;
        private BigDecimal currentBalance;
        private LocalDateTime updatedAt;

        public BalanceResponseBuilder accountId(Long accountId) {
            this.accountId = accountId;
            return this;
        }

        public BalanceResponseBuilder accountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
            return this;
        }

        public BalanceResponseBuilder currentBalance(BigDecimal currentBalance) {
            this.currentBalance = currentBalance;
            return this;
        }

        public BalanceResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BalanceResponse build() {
            return new BalanceResponse(accountId, accountHolderName, currentBalance, updatedAt);
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
