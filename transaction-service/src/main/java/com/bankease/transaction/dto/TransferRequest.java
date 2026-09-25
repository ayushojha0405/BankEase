package com.bankease.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Payload for transferring funds between two bank accounts")
public class TransferRequest {

    @NotNull(message = "Source account ID (fromAccountId) is required")
    @Schema(description = "Debit account ID", example = "1")
    private Long fromAccountId;

    @NotNull(message = "Destination account ID (toAccountId) is required")
    @Schema(description = "Credit account ID", example = "2")
    private Long toAccountId;

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "0.01", message = "Transfer amount must be strictly greater than 0")
    @Schema(description = "Amount to transfer", example = "1500.00")
    private BigDecimal amount;

    @Schema(description = "Optional transfer memo", example = "Rent payment")
    private String description;

    public TransferRequest() {
    }

    public TransferRequest(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
    }

    public static TransferRequestBuilder builder() {
        return new TransferRequestBuilder();
    }

    public static class TransferRequestBuilder {
        private Long fromAccountId;
        private Long toAccountId;
        private BigDecimal amount;
        private String description;

        public TransferRequestBuilder fromAccountId(Long fromAccountId) {
            this.fromAccountId = fromAccountId;
            return this;
        }

        public TransferRequestBuilder toAccountId(Long toAccountId) {
            this.toAccountId = toAccountId;
            return this;
        }

        public TransferRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransferRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TransferRequest build() {
            return new TransferRequest(fromAccountId, toAccountId, amount, description);
        }
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
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
