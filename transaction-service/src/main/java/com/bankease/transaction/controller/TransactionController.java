package com.bankease.transaction.controller;

import com.bankease.transaction.dto.*;
import com.bankease.transaction.service.TransactionService;
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
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction Management", description = "Operations for deposits, withdrawals, and two-party fund transfers")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds", description = "Credits money into a target account and records an immutable audit log.")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @Valid @RequestBody DepositRequest request) {
        log.info("REST: Deposit requested for account #{}, amount: {}", request.accountId(), request.amount());
        TransactionResponse response = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Deposit processed successfully", response));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw funds", description = "Debits money from an account if sufficient balance is available.")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @Valid @RequestBody WithdrawRequest request) {
        log.info("REST: Withdrawal requested for account #{}, amount: {}", request.accountId(), request.amount());
        TransactionResponse response = transactionService.withdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Withdrawal processed successfully", response));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds between accounts", description = "Executes an atomic 2-legged transfer between two distinct accounts with automatic compensation on downstream failure.")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransferRequest request) {
        log.info("REST: Transfer requested from #{} to #{}, amount: {}",
                request.fromAccountId(), request.toAccountId(), request.amount());
        TransactionResponse response = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Transfer processed successfully", response));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get transaction history for account", description = "Retrieves a paginated ledger of all incoming and outgoing transactions for an account.")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAccountTransactions(
            @Parameter(description = "Account ID", example = "1") @PathVariable("accountId") Long accountId,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TransactionResponse> page = transactionService.getAccountTransactions(accountId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Transactions retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieves details of a specific transaction record.")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
            @Parameter(description = "Transaction ID", example = "101") @PathVariable("id") Long id) {
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(ApiResponse.ok("Transaction retrieved successfully", response));
    }
}
