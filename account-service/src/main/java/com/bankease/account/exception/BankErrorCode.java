package com.bankease.account.exception;

public enum BankErrorCode {
    ACCOUNT_NOT_FOUND("ACC-4041", "Account identifier not found in ledger"),
    INSUFFICIENT_FUNDS("ACC-4001", "Requested debit exceeds available ledger balance"),
    CONCURRENT_MUTATION("ACC-4091", "Optimistic lock collision detected during balance mutation"),
    VALIDATION_FAILED("ACC-4002", "Request payload failed semantic validation constraints"),
    INTERNAL_ERROR("ACC-5001", "Internal ledger processing anomaly");

    private final String code;
    private final String defaultMessage;

    BankErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
