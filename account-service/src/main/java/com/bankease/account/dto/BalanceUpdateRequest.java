package com.bankease.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request body to debit or credit an account balance")
public record BalanceUpdateRequest(
    @NotNull(message = "Operation type is required (DEBIT or CREDIT)")
    @Schema(description = "Balance adjustment operation", example = "DEBIT")
    BalanceOperation operation,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be strictly greater than 0")
    @Schema(description = "Amount to debit or credit", example = "1000.00")
    BigDecimal amount,

    @Schema(description = "Optional transaction reference identifier", example = "TXN-1002")
    String reference
) {}
