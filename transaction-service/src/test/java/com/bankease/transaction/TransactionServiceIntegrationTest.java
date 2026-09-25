package com.bankease.transaction;

import com.bankease.transaction.client.AccountDto;
import com.bankease.transaction.client.AccountServiceClient;
import com.bankease.transaction.client.BalanceDto;
import com.bankease.transaction.dto.DepositRequest;
import com.bankease.transaction.dto.TransferRequest;
import com.bankease.transaction.dto.WithdrawRequest;
import com.bankease.transaction.entity.TransactionStatus;
import com.bankease.transaction.entity.TransactionType;
import com.bankease.transaction.exception.InsufficientFundsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class TransactionServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountServiceClient accountServiceClient;

    @Test
    @DisplayName("Should process deposit successfully")
    void testDepositSuccess() throws Exception {
        AccountDto account = new AccountDto(1L, "Vikram Seth", "SAVINGS", new BigDecimal("5000.00"), LocalDateTime.now());
        BalanceDto updatedBalance = new BalanceDto(1L, "Vikram Seth", new BigDecimal("7000.00"), LocalDateTime.now());

        Mockito.when(accountServiceClient.getAccount(1L)).thenReturn(account);
        Mockito.when(accountServiceClient.updateBalance(eq(1L), any())).thenReturn(updatedBalance);

        DepositRequest request = DepositRequest.builder()
                .accountId(1L)
                .amount(new BigDecimal("2000.00"))
                .description("Bonus deposit")
                .build();

        mockMvc.perform(post("/api/v1/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.toAccountId", is(1)))
                .andExpect(jsonPath("$.data.amount", is(2000.00)))
                .andExpect(jsonPath("$.data.transactionType", is(TransactionType.DEPOSIT.name())))
                .andExpect(jsonPath("$.data.status", is(TransactionStatus.SUCCESS.name())));
    }

    @Test
    @DisplayName("Should process withdrawal successfully")
    void testWithdrawSuccess() throws Exception {
        AccountDto account = new AccountDto(1L, "Vikram Seth", "SAVINGS", new BigDecimal("5000.00"), LocalDateTime.now());
        BalanceDto updatedBalance = new BalanceDto(1L, "Vikram Seth", new BigDecimal("4000.00"), LocalDateTime.now());

        Mockito.when(accountServiceClient.getAccount(1L)).thenReturn(account);
        Mockito.when(accountServiceClient.updateBalance(eq(1L), any())).thenReturn(updatedBalance);

        WithdrawRequest request = WithdrawRequest.builder()
                .accountId(1L)
                .amount(new BigDecimal("1000.00"))
                .description("ATM withdrawal")
                .build();

        mockMvc.perform(post("/api/v1/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.fromAccountId", is(1)))
                .andExpect(jsonPath("$.data.amount", is(1000.00)))
                .andExpect(jsonPath("$.data.transactionType", is(TransactionType.WITHDRAWAL.name())))
                .andExpect(jsonPath("$.data.status", is(TransactionStatus.SUCCESS.name())));
    }

    @Test
    @DisplayName("Should reject withdrawal when insufficient funds")
    void testWithdrawInsufficientFunds() throws Exception {
        AccountDto account = new AccountDto(1L, "Vikram Seth", "SAVINGS", new BigDecimal("500.00"), LocalDateTime.now());

        Mockito.when(accountServiceClient.getAccount(1L)).thenReturn(account);
        Mockito.when(accountServiceClient.updateBalance(eq(1L), any()))
                .thenThrow(new InsufficientFundsException("Insufficient funds in account ID 1. Current balance: 500.00"));

        WithdrawRequest request = WithdrawRequest.builder()
                .accountId(1L)
                .amount(new BigDecimal("2000.00"))
                .description("Overdraft attempt")
                .build();

        mockMvc.perform(post("/api/v1/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Insufficient Funds")));
    }

    @Test
    @DisplayName("Should execute 2-legged transfer between accounts successfully")
    void testTransferSuccess() throws Exception {
        AccountDto fromAcc = new AccountDto(1L, "Vikram Seth", "SAVINGS", new BigDecimal("5000.00"), LocalDateTime.now());
        AccountDto toAcc = new AccountDto(2L, "Ananya Rao", "CURRENT", new BigDecimal("1000.00"), LocalDateTime.now());

        Mockito.when(accountServiceClient.getAccount(1L)).thenReturn(fromAcc);
        Mockito.when(accountServiceClient.getAccount(2L)).thenReturn(toAcc);
        Mockito.when(accountServiceClient.updateBalance(eq(1L), any())).thenReturn(new BalanceDto());
        Mockito.when(accountServiceClient.updateBalance(eq(2L), any())).thenReturn(new BalanceDto());

        TransferRequest request = TransferRequest.builder()
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(new BigDecimal("1500.00"))
                .description("Consulting fee")
                .build();

        mockMvc.perform(post("/api/v1/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.fromAccountId", is(1)))
                .andExpect(jsonPath("$.data.toAccountId", is(2)))
                .andExpect(jsonPath("$.data.amount", is(1500.00)))
                .andExpect(jsonPath("$.data.transactionType", is(TransactionType.TRANSFER.name())))
                .andExpect(jsonPath("$.data.status", is(TransactionStatus.SUCCESS.name())));
    }

    @Test
    @DisplayName("Should reject self-transfer attempt")
    void testSelfTransferRejection() throws Exception {
        TransferRequest request = TransferRequest.builder()
                .fromAccountId(1L)
                .toAccountId(1L)
                .amount(new BigDecimal("500.00"))
                .build();

        mockMvc.perform(post("/api/v1/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Source and destination account IDs cannot be the same")));
    }
}
