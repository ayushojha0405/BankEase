package com.bankease.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Payload for transferring funds between two bank accounts")
public record TransferRequest(
    @NotNull(message = "Source account ID (fromAccountId) is required")
    @Schema(description = "Debit account ID", example = "1")
    Long fromAccountId,

    @NotNull(message = "Destination account ID (toAccountId) is required")
    @Schema(description = "Credit account ID", example = "2")
    Long toAccountId,

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "0.01", message = "Transfer amount must be strictly greater than zero")
    @Schema(description = "Amount to transfer", example = "1500.00")
    BigDecimal amount,

    @Schema(description = "Optional transfer memo", example = "Rent payment")
    String description
) {}
