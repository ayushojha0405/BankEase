package com.bankease.transaction.service;

import com.bankease.transaction.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    TransactionResponse deposit(DepositRequest request);
    TransactionResponse withdraw(WithdrawRequest request);
    TransactionResponse transfer(TransferRequest request);
    Page<TransactionResponse> getAccountTransactions(Long accountId, Pageable pageable);
    TransactionResponse getTransactionById(Long transactionId);
}
