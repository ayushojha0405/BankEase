package com.bankease.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Account balance response")
public record BalanceResponse(
    @Schema(description = "Account ID", example = "1")
    Long accountId,

    @Schema(description = "Account holder name", example = "Rahul Sharma")
    String accountHolderName,

    @Schema(description = "Current available balance", example = "4000.00")
    BigDecimal currentBalance,

    @Schema(description = "Last timestamp when balance was updated")
    LocalDateTime updatedAt
) {}
