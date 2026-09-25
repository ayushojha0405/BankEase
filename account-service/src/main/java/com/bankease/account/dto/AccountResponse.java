package com.bankease.account.dto;

import com.bankease.account.entity.Account;
import com.bankease.account.entity.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Account details response")
public record AccountResponse(
    @Schema(description = "Unique account ID", example = "1")
    Long accountId,

    @Schema(description = "Account holder full name", example = "Rahul Sharma")
    String accountHolderName,

    @Schema(description = "Account type", example = "SAVINGS")
    AccountType accountType,

    @Schema(description = "Current account balance", example = "5000.00")
    BigDecimal balance,

    @Schema(description = "Account creation timestamp")
    LocalDateTime createdAt,

    @Schema(description = "Last update timestamp")
    LocalDateTime updatedAt
) {
    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
            account.getAccountId(),
            account.getAccountHolderName(),
            account.getAccountType(),
            account.getBalance(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
}
