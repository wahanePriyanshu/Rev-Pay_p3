package com.revpay.transactionservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.revpay.transactionservice.client.UserClient.UserClient;
import com.revpay.transactionservice.dto.request.CreateMoneyRequestDto;
import com.revpay.transactionservice.dto.request.SendMoneyRequest;
import com.revpay.transactionservice.dto.request.InternalTransactionRequest;
import com.revpay.transactionservice.dto.response.MoneyRequestResponse;
import com.revpay.transactionservice.dto.response.TransactionResponse;
import com.revpay.transactionservice.service.TransactionService;

@RestController
public class TransactionController {

    private final TransactionService transactionService;
    private final UserClient userClient;

    public TransactionController(TransactionService transactionService, UserClient userClient) {
        this.transactionService = transactionService;
        this.userClient = userClient;
    }

    @PostMapping("/api/transfer/send")
    public TransactionResponse sendMoney(Authentication authentication,
                                         @RequestBody SendMoneyRequest request) {
        Long currentUserId = extractUserId(authentication);

        // Resolve receiver: if receiverUserId not already set, look up by email
        if (request.getReceiverUserId() == null && request.getTo() != null && !request.getTo().isBlank()) {
            try {
                UserClient.UserLookupResponse receiver = userClient.getUserByEmail(request.getTo());
                request.setReceiverUserId(receiver.resolvedId());
            } catch (Exception e) {
                throw new RuntimeException("Receiver not found for email: " + request.getTo());
            }
        }

        // Map note → description if description is blank
        if ((request.getDescription() == null || request.getDescription().isBlank()) && request.getNote() != null) {
            request.setDescription(request.getNote());
        }

        return transactionService.sendMoney(currentUserId, request);
    }

    @PostMapping("/api/transactions/internal/create")
    public void createInternalTransaction(@RequestBody InternalTransactionRequest request) {
        transactionService.createInternalTransaction(request);
    }

    @PostMapping("/api/requests")
    public MoneyRequestResponse createRequest(Authentication authentication,
                                              @RequestBody CreateMoneyRequestDto request) {
        Long currentUserId = extractUserId(authentication);
        return transactionService.createRequest(currentUserId, request);
    }

    @PostMapping("/api/requests/{id}/accept")
    public MoneyRequestResponse acceptRequest(Authentication authentication,
                                              @PathVariable Long id) {
        Long currentUserId = extractUserId(authentication);
        return transactionService.acceptRequest(currentUserId, id);
    }

    @PostMapping("/api/requests/{id}/decline")
    public MoneyRequestResponse declineRequest(Authentication authentication,
                                               @PathVariable Long id) {
        Long currentUserId = extractUserId(authentication);
        return transactionService.declineRequest(currentUserId, id);
    }

    @GetMapping("/api/requests/outgoing")
    public List<MoneyRequestResponse> getOutgoingRequests(Authentication authentication) {
        Long currentUserId = extractUserId(authentication);
        return transactionService.getOutgoingRequests(currentUserId);
    }

    @GetMapping("/api/requests/incoming")
    public List<MoneyRequestResponse> getIncomingRequests(Authentication authentication) {
        Long currentUserId = extractUserId(authentication);
        return transactionService.getIncomingRequests(currentUserId);
    }

    @GetMapping("/api/transactions/my")
    public List<TransactionResponse> getMyTransactions(Authentication authentication) {
        Long currentUserId = extractUserId(authentication);
        return transactionService.getMyTransactions(currentUserId);
    }

    @SuppressWarnings("unchecked")
    private Long extractUserId(Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Object value = details.get("userId");

        if (value instanceof Integer i) {
            return i.longValue();
        }
        if (value instanceof Long l) {
            return l;
        }
        return Long.valueOf(value.toString());
    }
}