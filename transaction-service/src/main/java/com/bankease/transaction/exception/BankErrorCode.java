package com.bankease.transaction.exception;

public enum BankErrorCode {
    ACCOUNT_NOT_FOUND("TXN-4041", "Account referenced in transaction does not exist"),
    INSUFFICIENT_FUNDS("TXN-4001", "Transaction rejected due to insufficient available funds"),
    INVALID_TRANSACTION("TXN-4002", "Transaction violates business validity constraints"),
    DOWNSTREAM_UNAVAILABLE("TXN-5031", "Core Account Service is unreachable or unavailable"),
    INTERNAL_ERROR("TXN-5001", "Internal transaction processing anomaly");

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
