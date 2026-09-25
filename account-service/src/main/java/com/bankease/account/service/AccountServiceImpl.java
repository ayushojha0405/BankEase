package com.bankease.account.service;

import com.bankease.account.dto.*;
import com.bankease.account.entity.Account;
import com.bankease.account.exception.AccountNotFoundException;
import com.bankease.account.exception.InsufficientBalanceException;
import com.bankease.account.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        log.info("Creating new account for holder: {}, type: {}, initial deposit: {}",
                request.getAccountHolderName(), request.getAccountType(), request.getInitialDeposit());

        Account account = Account.builder()
                .accountHolderName(request.getAccountHolderName().trim())
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO)
                .build();

        Account saved = accountRepository.save(account);
        log.info("Successfully created account with ID: {}", saved.getAccountId());
        return AccountResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long accountId) {
        log.debug("Fetching account details for ID: {}", accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return AccountResponse.fromEntity(account);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(Long accountId) {
        log.debug("Fetching balance for account ID: {}", accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return BalanceResponse.builder()
                .accountId(account.getAccountId())
                .accountHolderName(account.getAccountHolderName())
                .currentBalance(account.getBalance())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public BalanceResponse updateBalance(Long accountId, BalanceUpdateRequest request) {
        log.info("Updating balance for account ID: {}, operation: {}, amount: {}, reference: {}",
                accountId, request.getOperation(), request.getAmount(), request.getReference());

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance;

        if (request.getOperation() == BalanceOperation.DEBIT) {
            if (currentBalance.compareTo(request.getAmount()) < 0) {
                log.warn("Debit failed: Insufficient funds in account ID {}. Balance: {}, Requested: {}",
                        accountId, currentBalance, request.getAmount());
                throw new InsufficientBalanceException(accountId, currentBalance, request.getAmount());
            }
            newBalance = currentBalance.subtract(request.getAmount());
        } else if (request.getOperation() == BalanceOperation.CREDIT) {
            newBalance = currentBalance.add(request.getAmount());
        } else {
            throw new IllegalArgumentException("Unsupported balance operation: " + request.getOperation());
        }

        account.setBalance(newBalance);
        Account updated = accountRepository.save(account);

        log.info("Balance updated successfully for account ID: {}. Old: {}, New: {}",
                accountId, currentBalance, newBalance);

        return BalanceResponse.builder()
                .accountId(updated.getAccountId())
                .accountHolderName(updated.getAccountHolderName())
                .currentBalance(updated.getBalance())
                .updatedAt(updated.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponse> getAllAccounts(Pageable pageable) {
        log.debug("Fetching all accounts with pagination: {}", pageable);
        return accountRepository.findAll(pageable)
                .map(AccountResponse::fromEntity);
    }
}
