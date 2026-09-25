package com.bankease.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Payload for withdrawing funds from an account")
public record WithdrawRequest(
    @NotNull(message = "Account ID is required")
    @Schema(description = "Account ID to withdraw from", example = "1")
    Long accountId,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Withdrawal amount must be strictly greater than zero")
    @Schema(description = "Amount to withdraw", example = "1000.00")
    BigDecimal amount,

    @Schema(description = "Optional description / memo", example = "ATM cash withdrawal")
    String description
) {}
