package com.bankease.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Payload for depositing funds into an account")
public class DepositRequest {

    @NotNull(message = "Account ID is required")
    @Schema(description = "Account ID to deposit into", example = "1")
    private Long accountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Deposit amount must be strictly greater than 0")
    @Schema(description = "Amount to deposit", example = "2500.00")
    private BigDecimal amount;

    @Schema(description = "Optional description / memo", example = "Salary deposit")
    private String description;

    public DepositRequest() {
    }

    public DepositRequest(Long accountId, BigDecimal amount, String description) {
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
    }

    public static DepositRequestBuilder builder() {
        return new DepositRequestBuilder();
    }

    public static class DepositRequestBuilder {
        private Long accountId;
        private BigDecimal amount;
        private String description;

        public DepositRequestBuilder accountId(Long accountId) {
            this.accountId = accountId;
            return this;
        }

        public DepositRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public DepositRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public DepositRequest build() {
            return new DepositRequest(accountId, amount, description);
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
