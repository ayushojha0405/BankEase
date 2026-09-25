package com.bankease.account.service;

import com.bankease.account.dto.AccountCreateRequest;
import com.bankease.account.dto.AccountResponse;
import com.bankease.account.dto.BalanceResponse;
import com.bankease.account.dto.BalanceUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    AccountResponse createAccount(AccountCreateRequest request);
    AccountResponse getAccountById(Long accountId);
    BalanceResponse getBalance(Long accountId);
    BalanceResponse updateBalance(Long accountId, BalanceUpdateRequest request);
    Page<AccountResponse> getAllAccounts(Pageable pageable);
}
