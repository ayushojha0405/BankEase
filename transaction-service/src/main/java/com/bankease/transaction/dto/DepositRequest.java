package com.bankease.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Payload for depositing funds into an account")
public record DepositRequest(
    @NotNull(message = "Account ID is required")
    @Schema(description = "Account ID to deposit into", example = "1")
    Long accountId,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Deposit amount must be strictly greater than zero")
    @Schema(description = "Amount to deposit", example = "2500.00")
    BigDecimal amount,

    @Schema(description = "Optional description / memo", example = "Salary deposit")
    String description
) {}
