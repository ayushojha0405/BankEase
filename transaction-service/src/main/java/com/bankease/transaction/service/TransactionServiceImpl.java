package com.bankease.transaction.service;

import com.bankease.transaction.client.AccountServiceClient;
import com.bankease.transaction.client.BalanceOperation;
import com.bankease.transaction.client.BalanceUpdateRequestDto;
import com.bankease.transaction.dto.*;
import com.bankease.transaction.entity.Transaction;
import com.bankease.transaction.entity.TransactionStatus;
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
        log.info("Initiating DEPOSIT into account ID {}: amount {}", request.getAccountId(), request.getAmount());

        // 1. Verify destination account exists
        accountServiceClient.getAccount(request.getAccountId());

        // 2. Audit record as PENDING
        Transaction txn = Transaction.builder()
                .toAccountId(request.getAccountId())
                .amount(request.getAmount())
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .build();
        txn = transactionRepository.save(txn);

        try {
            // 3. Credit account in Account Service
            accountServiceClient.updateBalance(
                    request.getAccountId(),
                    new BalanceUpdateRequestDto(BalanceOperation.CREDIT, request.getAmount(), "TXN-" + txn.getTransactionId())
            );

            txn.setStatus(TransactionStatus.SUCCESS);
            log.info("Deposit successful for transaction ID: {}", txn.getTransactionId());
        } catch (Exception ex) {
            log.error("Deposit failed for transaction ID {}: {}", txn.getTransactionId(), ex.getMessage());
            txn.setStatus(TransactionStatus.FAILED);
            txn.setFailureReason(ex.getMessage());
            transactionRepository.save(txn);
            throw ex;
        }

        return TransactionResponse.fromEntity(transactionRepository.save(txn));
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {
        log.info("Initiating WITHDRAWAL from account ID {}: amount {}", request.getAccountId(), request.getAmount());

        // 1. Verify account exists
        accountServiceClient.getAccount(request.getAccountId());

        // 2. Audit record as PENDING
        Transaction txn = Transaction.builder()
                .fromAccountId(request.getAccountId())
                .amount(request.getAmount())
                .transactionType(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .build();
        txn = transactionRepository.save(txn);

        try {
            // 3. Debit balance in Account Service
            accountServiceClient.updateBalance(
                    request.getAccountId(),
                    new BalanceUpdateRequestDto(BalanceOperation.DEBIT, request.getAmount(), "TXN-" + txn.getTransactionId())
            );

            txn.setStatus(TransactionStatus.SUCCESS);
            log.info("Withdrawal successful for transaction ID: {}", txn.getTransactionId());
        } catch (Exception ex) {
            log.warn("Withdrawal failed for transaction ID {}: {}", txn.getTransactionId(), ex.getMessage());
            txn.setStatus(TransactionStatus.FAILED);
            txn.setFailureReason(ex.getMessage());
            transactionRepository.save(txn);
            throw ex;
        }

        return TransactionResponse.fromEntity(transactionRepository.save(txn));
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        log.info("Initiating TRANSFER from account {} to account {}: amount {}",
                request.getFromAccountId(), request.getToAccountId(), request.getAmount());

        // 1. Guard against self-transfer
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new InvalidTransactionException("Source and destination account IDs cannot be the same.");
        }

        // 2. Verify both accounts exist in Account Service
        accountServiceClient.getAccount(request.getFromAccountId());
        accountServiceClient.getAccount(request.getToAccountId());

        // 3. Record transaction as PENDING in transaction ledger
        Transaction txn = Transaction.builder()
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .build();
        txn = transactionRepository.save(txn);

        // 4. Step 1: Debit source account
        try {
            accountServiceClient.updateBalance(
                    request.getFromAccountId(),
                    new BalanceUpdateRequestDto(BalanceOperation.DEBIT, request.getAmount(), "TXN-" + txn.getTransactionId())
            );
        } catch (Exception debitEx) {
            log.warn("Transfer debit failed for transaction ID {}: {}", txn.getTransactionId(), debitEx.getMessage());
            txn.setStatus(TransactionStatus.FAILED);
            txn.setFailureReason("Debit failed: " + debitEx.getMessage());
            transactionRepository.save(txn);
            throw debitEx;
        }

        // 5. Step 2: Credit destination account (with compensating rollback on failure)
        try {
            accountServiceClient.updateBalance(
                    request.getToAccountId(),
                    new BalanceUpdateRequestDto(BalanceOperation.CREDIT, request.getAmount(), "TXN-" + txn.getTransactionId())
            );
        } catch (Exception creditEx) {
            log.error("Transfer credit to account {} failed after successful debit. Executing compensation refund.",
                    request.getToAccountId(), creditEx);

            try {
                // Compensating Action: Refund debited amount back to source account
                accountServiceClient.updateBalance(
                        request.getFromAccountId(),
                        new BalanceUpdateRequestDto(BalanceOperation.CREDIT, request.getAmount(), "TXN-REFUND-" + txn.getTransactionId())
                );
                txn.setFailureReason("Credit to recipient failed. Funds refunded back to source account: " + creditEx.getMessage());
            } catch (Exception refundEx) {
                log.error("CRITICAL: Compensation refund failed for transaction ID {}: {}", txn.getTransactionId(), refundEx.getMessage());
                txn.setFailureReason("Credit failed and auto-refund failed. Manual reconciliation required: " + refundEx.getMessage());
            }

            txn.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(txn);
            throw creditEx;
        }

        // 6. Complete transaction
        txn.setStatus(TransactionStatus.SUCCESS);
        log.info("Transfer completed successfully for transaction ID: {}", txn.getTransactionId());
        return TransactionResponse.fromEntity(transactionRepository.save(txn));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAccountTransactions(Long accountId, Pageable pageable) {
        log.debug("Fetching transactions for account ID: {}", accountId);
        return transactionRepository.findByAccountId(accountId, pageable)
                .map(TransactionResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long transactionId) {
        log.debug("Fetching transaction by ID: {}", transactionId);
        Transaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new InvalidTransactionException("Transaction not found with ID: " + transactionId));
        return TransactionResponse.fromEntity(txn);
    }
}
