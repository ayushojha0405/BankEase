package com.bankease.account.dto;

import com.bankease.account.entity.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Request body for creating a new bank account")
public class AccountCreateRequest {

    @NotBlank(message = "Account holder name is required")
    @Size(min = 2, max = 100, message = "Account holder name must be between 2 and 100 characters")
    @Schema(description = "Full name of the account holder", example = "Rahul Sharma")
    private String accountHolderName;

    @NotNull(message = "Account type is required (SAVINGS or CURRENT)")
    @Schema(description = "Type of bank account", example = "SAVINGS")
    private AccountType accountType;

    @NotNull(message = "Initial deposit amount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Initial deposit cannot be negative")
    @Schema(description = "Initial deposit amount (>= 0.00)", example = "5000.00")
    private BigDecimal initialDeposit;

    public AccountCreateRequest() {
    }

    public AccountCreateRequest(String accountHolderName, AccountType accountType, BigDecimal initialDeposit) {
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.initialDeposit = initialDeposit;
    }

    public static AccountCreateRequestBuilder builder() {
        return new AccountCreateRequestBuilder();
    }

    public static class AccountCreateRequestBuilder {
        private String accountHolderName;
        private AccountType accountType;
        private BigDecimal initialDeposit;

        public AccountCreateRequestBuilder accountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
            return this;
        }

        public AccountCreateRequestBuilder accountType(AccountType accountType) {
            this.accountType = accountType;
            return this;
        }

        public AccountCreateRequestBuilder initialDeposit(BigDecimal initialDeposit) {
            this.initialDeposit = initialDeposit;
            return this;
        }

        public AccountCreateRequest build() {
            return new AccountCreateRequest(accountHolderName, accountType, initialDeposit);
        }
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
    }
}
