package com.bankease.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request body to debit or credit an account balance")
public class BalanceUpdateRequest {

    @NotNull(message = "Operation type is required (DEBIT or CREDIT)")
    @Schema(description = "Balance adjustment operation", example = "DEBIT")
    private BalanceOperation operation;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be strictly greater than 0")
    @Schema(description = "Amount to debit or credit", example = "1000.00")
    private BigDecimal amount;

    @Schema(description = "Optional transaction reference identifier", example = "TXN-1002")
    private String reference;

    public BalanceUpdateRequest() {
    }

    public BalanceUpdateRequest(BalanceOperation operation, BigDecimal amount, String reference) {
        this.operation = operation;
        this.amount = amount;
        this.reference = reference;
    }

    public static BalanceUpdateRequestBuilder builder() {
        return new BalanceUpdateRequestBuilder();
    }

    public static class BalanceUpdateRequestBuilder {
        private BalanceOperation operation;
        private BigDecimal amount;
        private String reference;

        public BalanceUpdateRequestBuilder operation(BalanceOperation operation) {
            this.operation = operation;
            return this;
        }

        public BalanceUpdateRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public BalanceUpdateRequestBuilder reference(String reference) {
            this.reference = reference;
            return this;
        }

        public BalanceUpdateRequest build() {
            return new BalanceUpdateRequest(operation, amount, reference);
        }
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
