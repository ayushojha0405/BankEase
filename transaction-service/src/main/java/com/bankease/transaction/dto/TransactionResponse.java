package com.bankease.transaction.dto;

import com.bankease.transaction.entity.Transaction;
import com.bankease.transaction.entity.TransactionStatus;
import com.bankease.transaction.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Transaction audit and ledger record response")
public record TransactionResponse(
    @Schema(description = "Unique transaction ID", example = "101")
    Long transactionId,

    @Schema(description = "Debit account ID (null for deposits)", example = "1")
    Long fromAccountId,

    @Schema(description = "Credit account ID (null for withdrawals)", example = "2")
    Long toAccountId,

    @Schema(description = "Transaction amount", example = "1500.00")
    BigDecimal amount,

    @Schema(description = "Transaction type (DEPOSIT, WITHDRAWAL, TRANSFER)", example = "TRANSFER")
    TransactionType transactionType,

    @Schema(description = "Status of transaction (PENDING, SUCCESS, FAILED)", example = "SUCCESS")
    TransactionStatus status,

    @Schema(description = "Reason if transaction failed")
    String failureReason,

    @Schema(description = "Description or memo", example = "Rent payment")
    String description,

    @Schema(description = "Timestamp when the transaction was executed")
    LocalDateTime timestamp
) {
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
}
