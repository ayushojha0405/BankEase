package com.bankease.transaction.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long accountId) {
        super(String.format("Account with ID %d does not exist in Account Service", accountId));
    }
}
