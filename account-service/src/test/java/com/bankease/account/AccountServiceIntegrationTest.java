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
        AccountCreateRequest request = new AccountCreateRequest(
                "Amit Patel",
                AccountType.SAVINGS,
                new BigDecimal("10000.00")
        );

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
        AccountCreateRequest request = new AccountCreateRequest(
                "Priya Verma",
                AccountType.CURRENT,
                new BigDecimal("2000.00")
        );

        String response = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Number accountIdNum = com.jayway.jsonpath.JsonPath.read(response, "$.data.accountId");
        long accountId = accountIdNum.longValue();

        // 1. Successful debit
        BalanceUpdateRequest debitReq = new BalanceUpdateRequest(
                BalanceOperation.DEBIT,
                new BigDecimal("500.00"),
                "REF-101"
        );

        mockMvc.perform(put("/api/v1/accounts/" + accountId + "/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debitReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentBalance", is(1500.00)));

        // 2. Insufficient balance debit attempt
        BalanceUpdateRequest overDebit = new BalanceUpdateRequest(
                BalanceOperation.DEBIT,
                new BigDecimal("2000.00"),
                "REF-102"
        );

        mockMvc.perform(put("/api/v1/accounts/" + accountId + "/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overDebit)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("ACC-4001")))
                .andExpect(jsonPath("$.error", is("Insufficient Funds")));
    }

    @Test
    @DisplayName("Should return 404 when querying non-existent account")
    void testAccountNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.errorCode", is("ACC-4041")))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }
}
