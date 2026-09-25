package com.bankease.transaction.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDto(
    Long accountId,
    String accountHolderName,
    String accountType,
    BigDecimal balance,
    LocalDateTime createdAt
) {}
