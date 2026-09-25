package com.bankease.transaction.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BalanceDto(
    Long accountId,
    String accountHolderName,
    BigDecimal currentBalance,
    LocalDateTime updatedAt
) {}
