package com.bankease.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Payload for withdrawing funds from an account")
public class WithdrawRequest {

    @NotNull(message = "Account ID is required")
    @Schema(description = "Account ID to withdraw from", example = "1")
    private Long accountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Withdrawal amount must be strictly greater than 0")
    @Schema(description = "Amount to withdraw", example = "1000.00")
    private BigDecimal amount;

    @Schema(description = "Optional description / memo", example = "ATM cash withdrawal")
    private String description;

    public WithdrawRequest() {
    }

    public WithdrawRequest(Long accountId, BigDecimal amount, String description) {
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
    }

    public static WithdrawRequestBuilder builder() {
        return new WithdrawRequestBuilder();
    }

    public static class WithdrawRequestBuilder {
        private Long accountId;
        private BigDecimal amount;
        private String description;

        public WithdrawRequestBuilder accountId(Long accountId) {
            this.accountId = accountId;
            return this;
        }

        public WithdrawRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public WithdrawRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public WithdrawRequest build() {
            return new WithdrawRequest(accountId, amount, description);
        }
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
