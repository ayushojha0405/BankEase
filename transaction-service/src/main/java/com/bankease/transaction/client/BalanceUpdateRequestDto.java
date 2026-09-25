package com.bankease.transaction.client;

import java.math.BigDecimal;

public record BalanceUpdateRequestDto(
    BalanceOperation operation,
    BigDecimal amount,
    String reference
) {}
