package com.bankease.transaction.client;

import com.bankease.transaction.exception.AccountNotFoundException;
import com.bankease.transaction.exception.AccountServiceUnavailableException;
import com.bankease.transaction.exception.InsufficientFundsException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class AccountServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public AccountServiceClient(
            @Value("${bankease.account-service.base-url:http://localhost:8081}") String baseUrl,
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public AccountDto getAccount(Long accountId) {
        try {
            String rawJson = restClient.get()
                    .uri("/api/v1/accounts/{id}", accountId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            throw new AccountNotFoundException(accountId);
                        }
                        handleClientError(response.getBody().readAllBytes());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new AccountServiceUnavailableException("Account Service returned 5xx server error");
                    })
                    .body(String.class);

            JsonNode dataNode = objectMapper.readTree(rawJson).get("data");
            return objectMapper.treeToValue(dataNode, AccountDto.class);
        } catch (ResourceAccessException ex) {
            log.error("Network I/O failure communicating with Account Service: {}", ex.getMessage());
            throw new AccountServiceUnavailableException("Account Service is unreachable on port 8081", ex);
        } catch (AccountNotFoundException | AccountServiceUnavailableException | InsufficientFundsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in AccountServiceClient: {}", ex.getMessage(), ex);
            throw new AccountServiceUnavailableException("Communication error with Account Service: " + ex.getMessage(), ex);
        }
    }

    public BalanceDto updateBalance(Long accountId, BalanceUpdateRequestDto request) {
        try {
            String rawJson = restClient.put()
                    .uri("/api/v1/accounts/{id}/balance", accountId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (clientReq, response) -> {
                        byte[] body = response.getBody().readAllBytes();
                        if (response.getStatusCode().value() == 404) {
                            throw new AccountNotFoundException(accountId);
                        }
                        handleClientError(body);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (clientReq, response) -> {
                        throw new AccountServiceUnavailableException("Downstream Account Service failed during balance mutation");
                    })
                    .body(String.class);

            JsonNode dataNode = objectMapper.readTree(rawJson).get("data");
            return objectMapper.treeToValue(dataNode, BalanceDto.class);
        } catch (ResourceAccessException ex) {
            log.error("Network I/O failure updating balance in Account Service: {}", ex.getMessage());
            throw new AccountServiceUnavailableException("Account Service connection timed out or unreachable.", ex);
        } catch (AccountNotFoundException | AccountServiceUnavailableException | InsufficientFundsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating balance on Account Service: {}", ex.getMessage(), ex);
            throw new AccountServiceUnavailableException("Error during balance mutation: " + ex.getMessage(), ex);
        }
    }

    private void handleClientError(byte[] responseBody) {
        try {
            String text = new String(responseBody);
            JsonNode node = objectMapper.readTree(text);
            String message = node.has("message") ? node.get("message").asText() : text;
            String error = node.has("error") ? node.get("error").asText() : "";
            if (error.equalsIgnoreCase("Insufficient Funds") || message.toLowerCase().contains("insufficient")) {
                throw new InsufficientFundsException(message);
            }
            throw new RuntimeException("Account Service returned client error: " + message);
        } catch (InsufficientFundsException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Error parsing Account Service error payload: " + ex.getMessage());
        }
    }
}
