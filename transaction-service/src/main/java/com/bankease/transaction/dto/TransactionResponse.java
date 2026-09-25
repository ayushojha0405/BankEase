package com.bankease.transaction.dto;

import com.bankease.transaction.entity.Transaction;
import com.bankease.transaction.entity.TransactionStatus;
import com.bankease.transaction.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Transaction audit and ledger record response")
public class TransactionResponse {

    @Schema(description = "Unique transaction ID", example = "101")
    private Long transactionId;

    @Schema(description = "Debit account ID (null for deposits)", example = "1")
    private Long fromAccountId;

    @Schema(description = "Credit account ID (null for withdrawals)", example = "2")
    private Long toAccountId;

    @Schema(description = "Transaction amount", example = "1500.00")
    private BigDecimal amount;

    @Schema(description = "Transaction type (DEPOSIT, WITHDRAWAL, TRANSFER)", example = "TRANSFER")
    private TransactionType transactionType;

    @Schema(description = "Status of transaction (PENDING, SUCCESS, FAILED)", example = "SUCCESS")
    private TransactionStatus status;

    @Schema(description = "Reason if transaction failed")
    private String failureReason;

    @Schema(description = "Description or memo", example = "Rent payment")
    private String description;

    @Schema(description = "Timestamp when the transaction was executed")
    private LocalDateTime timestamp;

    public TransactionResponse() {
    }

    public TransactionResponse(Long transactionId, Long fromAccountId, Long toAccountId, BigDecimal amount,
                               TransactionType transactionType, TransactionStatus status,
                               String failureReason, String description, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = status;
        this.failureReason = failureReason;
        this.description = description;
        this.timestamp = timestamp;
    }

    public static TransactionResponse fromEntity(Transaction t) {
        return new TransactionResponse(
                t.getTransactionId(),
                t.getFromAccountId(),
                t.getToAccountId(),
                t.getAmount(),
                t.getTransactionType(),
                t.getStatus(),
                t.getFailureReason(),
                t.getDescription(),
                t.getTimestamp()
        );
    }

    public static TransactionResponseBuilder builder() {
        return new TransactionResponseBuilder();
    }

    public static class TransactionResponseBuilder {
        private Long transactionId;
        private Long fromAccountId;
        private Long toAccountId;
        private BigDecimal amount;
        private TransactionType transactionType;
        private TransactionStatus status;
        private String failureReason;
        private String description;
        private LocalDateTime timestamp;

        public TransactionResponseBuilder transactionId(Long transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionResponseBuilder fromAccountId(Long fromAccountId) {
            this.fromAccountId = fromAccountId;
            return this;
        }

        public TransactionResponseBuilder toAccountId(Long toAccountId) {
            this.toAccountId = toAccountId;
            return this;
        }

        public TransactionResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionResponseBuilder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public TransactionResponseBuilder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public TransactionResponseBuilder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public TransactionResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TransactionResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public TransactionResponse build() {
            return new TransactionResponse(transactionId, fromAccountId, toAccountId, amount, transactionType, status, failureReason, description, timestamp);
        }
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
