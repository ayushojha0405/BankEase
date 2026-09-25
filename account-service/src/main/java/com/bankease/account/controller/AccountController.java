package com.bankease.account.controller;

import com.bankease.account.dto.*;
import com.bankease.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Management", description = "Operations for creating, fetching, and updating core bank accounts")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @Operation(summary = "Create a new bank account", description = "Opens a new SAVINGS or CURRENT account with an optional initial deposit.")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody AccountCreateRequest request) {
        log.info("REST: Request received to open account for customer '{}'", request.accountHolderName());
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Account created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account details by ID", description = "Retrieves full account metadata and balance for a specific account ID.")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(
            @Parameter(description = "Account ID", example = "1") @PathVariable("id") Long id) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.ok("Account retrieved successfully", response));
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Get current account balance", description = "Fetches the current real-time ledger balance for an account.")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(
            @Parameter(description = "Account ID", example = "1") @PathVariable("id") Long id) {
        BalanceResponse response = accountService.getBalance(id);
        return ResponseEntity.ok(ApiResponse.ok("Balance fetched successfully", response));
    }

    @PutMapping("/{id}/balance")
    @Operation(summary = "Update account balance (Internal REST call)", description = "Debits or credits an account balance. Invoked synchronously by Transaction Service.")
    public ResponseEntity<ApiResponse<BalanceResponse>> updateBalance(
            @Parameter(description = "Account ID", example = "1") @PathVariable("id") Long id,
            @Valid @RequestBody BalanceUpdateRequest request) {
        log.info("REST: Internal balance update request on account #{}: {} {}", id, request.operation(), request.amount());
        BalanceResponse response = accountService.updateBalance(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Balance updated successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all accounts", description = "Retrieves a paginated list of all accounts.")
    public ResponseEntity<ApiResponse<Page<AccountResponse>>> getAllAccounts(
            @PageableDefault(size = 20, sort = "accountId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AccountResponse> page = accountService.getAllAccounts(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Accounts retrieved successfully", page));
    }
}
