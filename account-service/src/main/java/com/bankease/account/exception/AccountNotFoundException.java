package com.bankease.account.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long accountId) {
        super(String.format("Account not found with ID: %d", accountId));
    }
}
