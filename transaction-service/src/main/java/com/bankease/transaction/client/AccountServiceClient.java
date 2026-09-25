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
        log.info("Initialized AccountServiceClient pointing to base URL: {}", baseUrl);
    }

    public AccountDto getAccount(Long accountId) {
        log.debug("Calling Account Service GET /api/v1/accounts/{}", accountId);
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
                        throw new AccountServiceUnavailableException("Account Service returned 5xx error");
                    })
                    .body(String.class);

            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode dataNode = root.get("data");
            return objectMapper.treeToValue(dataNode, AccountDto.class);
        } catch (ResourceAccessException ex) {
            log.error("Failed to connect to Account Service: {}", ex.getMessage());
            throw new AccountServiceUnavailableException("Account Service is unreachable. Please verify it is running on port 8081.", ex);
        } catch (AccountNotFoundException | AccountServiceUnavailableException | InsufficientFundsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error calling Account Service: {}", ex.getMessage(), ex);
            throw new AccountServiceUnavailableException("Error communicating with Account Service: " + ex.getMessage(), ex);
        }
    }

    public BalanceDto updateBalance(Long accountId, BalanceUpdateRequestDto request) {
        log.info("Calling Account Service PUT /api/v1/accounts/{}/balance with op: {}, amount: {}",
                accountId, request.getOperation(), request.getAmount());
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
                        throw new AccountServiceUnavailableException("Account Service failed during balance update");
                    })
                    .body(String.class);

            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode dataNode = root.get("data");
            return objectMapper.treeToValue(dataNode, BalanceDto.class);
        } catch (ResourceAccessException ex) {
            log.error("Failed to connect to Account Service: {}", ex.getMessage());
            throw new AccountServiceUnavailableException("Account Service is unreachable during balance update.", ex);
        } catch (AccountNotFoundException | AccountServiceUnavailableException | InsufficientFundsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating balance on Account Service: {}", ex.getMessage(), ex);
            throw new AccountServiceUnavailableException("Error communicating with Account Service: " + ex.getMessage(), ex);
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
            throw new RuntimeException("Account Service client error: " + message);
        } catch (InsufficientFundsException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Error parsing Account Service error: " + ex.getMessage());
        }
    }
}
