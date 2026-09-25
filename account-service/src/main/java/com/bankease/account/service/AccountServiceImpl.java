package com.bankease.account.service;

import com.bankease.account.dto.*;
import com.bankease.account.entity.Account;
import com.bankease.account.exception.AccountNotFoundException;
import com.bankease.account.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("Opening new {} account for customer: {}", request.accountType(), request.accountHolderName());

        Account account = new Account(
                request.accountHolderName(),
                request.accountType(),
                request.initialDeposit()
        );

        Account saved = accountRepository.save(account);
        log.info("Account successfully created with ID: {}", saved.getAccountId());
        return AccountResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .map(AccountResponse::fromEntity)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        return new BalanceResponse(
                account.getAccountId(),
                account.getAccountHolderName(),
                account.getBalance(),
                account.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public BalanceResponse updateBalance(Long accountId, BalanceUpdateRequest request) {
        log.info("Processing balance update on account #{}: op={}, amount={}, ref={}",
                accountId, request.operation(), request.amount(), request.reference());

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // Delegate to rich domain model to enforce business invariants
        if (request.operation() == BalanceOperation.DEBIT) {
            account.debit(request.amount());
        } else if (request.operation() == BalanceOperation.CREDIT) {
            account.credit(request.amount());
        } else {
            throw new IllegalArgumentException("Unsupported balance operation: " + request.operation());
        }

        // Hibernate automatically checks @Version upon flush/commit to protect against concurrent writes
        Account updated = accountRepository.save(account);

        return new BalanceResponse(
                updated.getAccountId(),
                updated.getAccountHolderName(),
                updated.getBalance(),
                updated.getUpdatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponse> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(AccountResponse::fromEntity);
    }
}
