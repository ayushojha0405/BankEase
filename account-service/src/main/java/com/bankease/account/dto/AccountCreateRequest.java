package com.bankease.account.dto;

import com.bankease.account.entity.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Request body for opening a new bank account")
public record AccountCreateRequest(
    @NotBlank(message = "Account holder name is required")
    @Size(min = 2, max = 100, message = "Account holder name must be between 2 and 100 characters")
    @Schema(description = "Full name of the account holder", example = "Rahul Sharma")
    String accountHolderName,

    @NotNull(message = "Account type is required (SAVINGS or CURRENT)")
    @Schema(description = "Type of bank account", example = "SAVINGS")
    AccountType accountType,

    @NotNull(message = "Initial deposit amount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Initial deposit cannot be negative")
    @Schema(description = "Initial deposit amount (>= 0.00)", example = "5000.00")
    BigDecimal initialDeposit
) {}
