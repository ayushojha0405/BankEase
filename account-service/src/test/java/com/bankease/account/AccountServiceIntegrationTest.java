package com.bankease.account;

import com.bankease.account.dto.AccountCreateRequest;
import com.bankease.account.dto.BalanceOperation;
import com.bankease.account.dto.BalanceUpdateRequest;
import com.bankease.account.entity.AccountType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AccountServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully create a new bank account")
    void testCreateAccount() throws Exception {
        AccountCreateRequest request = AccountCreateRequest.builder()
                .accountHolderName("Amit Patel")
                .accountType(AccountType.SAVINGS)
                .initialDeposit(new BigDecimal("10000.00"))
                .build();

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accountId", notNullValue()))
                .andExpect(jsonPath("$.data.accountHolderName", is("Amit Patel")))
                .andExpect(jsonPath("$.data.accountType", is("SAVINGS")))
                .andExpect(jsonPath("$.data.balance", is(10000.00)));
    }

    @Test
    @DisplayName("Should debit balance and reject debit on insufficient funds")
    void testBalanceDebitAndInsufficientFunds() throws Exception {
        // 1. Create account with 2000.00
        AccountCreateRequest request = AccountCreateRequest.builder()
                .accountHolderName("Priya Verma")
                .accountType(AccountType.CURRENT)
                .initialDeposit(new BigDecimal("2000.00"))
                .build();

        String response = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Number accountIdNum = com.jayway.jsonpath.JsonPath.read(response, "$.data.accountId");
        long accountId = accountIdNum.longValue();

        // 2. Successful debit 500.00 -> balance becomes 1500.00
        BalanceUpdateRequest debitReq = BalanceUpdateRequest.builder()
                .operation(BalanceOperation.DEBIT)
                .amount(new BigDecimal("500.00"))
                .reference("REF-101")
                .build();

        mockMvc.perform(put("/api/v1/accounts/" + accountId + "/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debitReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentBalance", is(1500.00)));

        // 3. Failed debit 2000.00 (insufficient balance)
        BalanceUpdateRequest overDebit = BalanceUpdateRequest.builder()
                .operation(BalanceOperation.DEBIT)
                .amount(new BigDecimal("2000.00"))
                .reference("REF-102")
                .build();

        mockMvc.perform(put("/api/v1/accounts/" + accountId + "/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overDebit)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Insufficient Funds")));
    }

    @Test
    @DisplayName("Should return 404 when querying non-existent account")
    void testAccountNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }
}
