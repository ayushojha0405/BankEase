package com.bankease.transaction.service;

import com.bankease.transaction.client.AccountServiceClient;
import com.bankease.transaction.client.BalanceOperation;
import com.bankease.transaction.client.BalanceUpdateRequestDto;
import com.bankease.transaction.dto.*;
import com.bankease.transaction.entity.Transaction;
import com.bankease.transaction.entity.TransactionType;
import com.bankease.transaction.exception.InvalidTransactionException;
import com.bankease.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountServiceClient accountServiceClient) {
        this.transactionRepository = transactionRepository;
        this.accountServiceClient = accountServiceClient;
    }

    @Override
    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        log.info("Deposit initiated: target account #{}, amount: {}", request.accountId(), request.amount());

        // Validate account existence against source-of-truth service
        accountServiceClient.getAccount(request.accountId());

        Transaction txn = new Transaction(
                null,
                request.accountId(),
                request.amount(),
                TransactionType.DEPOSIT,
                request.description()
        );
        txn = transactionRepository.save(txn);

        try {
            accountServiceClient.updateBalance(
                    request.accountId(),
                    new BalanceUpdateRequestDto(BalanceOperation.CREDIT, request.amount(), "TXN-" + txn.getTransactionId())
            );
            txn.markSuccess();
            log.info("Deposit completed successfully for transaction ID: {}", txn.getTransactionId());
        } catch (Exception ex) {
            log.error("Deposit failed for transaction ID {}: {}", txn.getTransactionId(), ex.getMessage());
            txn.markFailed(ex.getMessage());
            transactionRepository.save(txn);
            throw ex;
        }

        return TransactionResponse.fromEntity(transactionRepository.save(txn));
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {
        log.info("Withdrawal initiated: source account #{}, amount: {}", request.accountId(), request.amount());

        // Validate account existence
        accountServiceClient.getAccount(request.accountId());

        Transaction txn = new Transaction(
                request.accountId(),
                null,
                request.amount(),
                TransactionType.WITHDRAWAL,
                request.description()
        );
        txn = transactionRepository.save(txn);

        try {
            accountServiceClient.updateBalance(
                    request.accountId(),
                    new BalanceUpdateRequestDto(BalanceOperation.DEBIT, request.amount(), "TXN-" + txn.getTransactionId())
            );
            txn.markSuccess();
            log.info("Withdrawal completed successfully for transaction ID: {}", txn.getTransactionId());
        } catch (Exception ex) {
            log.warn("Withdrawal rejected for transaction ID {}: {}", txn.getTransactionId(), ex.getMessage());
            txn.markFailed(ex.getMessage());
            transactionRepository.save(txn);
            throw ex;
        }

        return TransactionResponse.fromEntity(transactionRepository.save(txn));
    }

    /**
     * Orchestrates a 2-legged cross-service transfer using a Compensating Transaction pattern.
     * Note: In high-throughput banking systems, this pattern replaces heavy 2PC locks by issuing
     * an automatic reversing refund if downstream credit fails after source debit has succeeded.
     */
    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        log.info("Transfer initiated: from account #{} to #{}, amount: {}",
                request.fromAccountId(), request.toAccountId(), request.amount());

        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new InvalidTransactionException("Source and destination account IDs cannot be the same.");
        }

        // Verify both accounts exist prior to initiating debit leg
        accountServiceClient.getAccount(request.fromAccountId());
        accountServiceClient.getAccount(request.toAccountId());

        Transaction txn = new Transaction(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount(),
                TransactionType.TRANSFER,
                request.description()
        );
        txn = transactionRepository.save(txn);

        // Leg 1: Debit source account
        try {
            accountServiceClient.updateBalance(
                    request.fromAccountId(),
                    new BalanceUpdateRequestDto(BalanceOperation.DEBIT, request.amount(), "TXN-" + txn.getTransactionId())
            );
        } catch (Exception debitEx) {
            log.warn("Transfer debit leg failed for transaction ID {}: {}", txn.getTransactionId(), debitEx.getMessage());
            txn.markFailed("Debit failed: " + debitEx.getMessage());
            transactionRepository.save(txn);
            throw debitEx;
        }

        // Leg 2: Credit destination account (with compensating rollback guard)
        try {
            accountServiceClient.updateBalance(
                    request.toAccountId(),
                    new BalanceUpdateRequestDto(BalanceOperation.CREDIT, request.amount(), "TXN-" + txn.getTransactionId())
            );
        } catch (Exception creditEx) {
            log.error("Transfer credit leg failed for transaction ID {}. Executing compensating refund to source account #{}",
                    txn.getTransactionId(), request.fromAccountId(), creditEx);

            try {
                // Compensating action: refund debited amount back to sender
                accountServiceClient.updateBalance(
                        request.fromAccountId(),
                        new BalanceUpdateRequestDto(BalanceOperation.CREDIT, request.amount(), "TXN-REFUND-" + txn.getTransactionId())
                );
                txn.markFailed("Credit to recipient failed. Funds refunded back to source: " + creditEx.getMessage());
            } catch (Exception compensationEx) {
                log.error("CRITICAL: Automated compensation refund failed for transaction #{}. Flagged for manual reconciliation.",
                        txn.getTransactionId(), compensationEx);
                txn.markFailed("Credit failed and auto-refund failed. Manual reconciliation required: " + compensationEx.getMessage());
            }

            transactionRepository.save(txn);
            throw creditEx;
        }

        txn.markSuccess();
        log.info("Transfer completed successfully for transaction ID: {}", txn.getTransactionId());
        return TransactionResponse.fromEntity(transactionRepository.save(txn));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAccountTransactions(Long accountId, Pageable pageable) {
        return transactionRepository.findByAccountId(accountId, pageable)
                .map(TransactionResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(TransactionResponse::fromEntity)
                .orElseThrow(() -> new InvalidTransactionException("Transaction not found with ID: " + transactionId));
    }
}
