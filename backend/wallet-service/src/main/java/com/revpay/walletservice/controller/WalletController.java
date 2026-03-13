package com.revpay.walletservice.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.revpay.walletservice.dto.request.AddMoneyRequest;
import com.revpay.walletservice.dto.request.WalletOperationRequest;
import com.revpay.walletservice.dto.request.WithdrawMoneyRequest;
import com.revpay.walletservice.dto.response.WalletBalanceResponse;
import com.revpay.walletservice.dto.response.WalletResponse;
import com.revpay.walletservice.service.WalletService;

@RestController
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/api/wallet/me")
    public WalletResponse getMyWallet(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return walletService.getMyWallet(userId);
    }

    @GetMapping("/api/wallet/balance")
    public WalletBalanceResponse getMyBalance(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return walletService.getMyBalance(userId);
    }

    @PostMapping("/api/wallet/add")
    public WalletResponse addMoney(Authentication authentication,
                                   @RequestBody AddMoneyRequest request) {
        Long userId = extractUserId(authentication);
        return walletService.addMoney(userId, request);
    }

    @PostMapping("/api/wallet/withdraw")
    public WalletResponse withdrawMoney(Authentication authentication,
                                        @RequestBody WithdrawMoneyRequest request) {
        Long userId = extractUserId(authentication);
        return walletService.withdrawMoney(userId, request);
    }

    @PostMapping("/api/wallet/internal/debit")
    public Map<String, String> debitInternal(@RequestBody WalletOperationRequest request) {
        walletService.debitInternal(request);
        return Map.of("message", "Wallet debited successfully");
    }

    @PostMapping("/api/wallet/internal/credit")
    public Map<String, String> creditInternal(@RequestBody WalletOperationRequest request) {
        walletService.creditInternal(request);
        return Map.of("message", "Wallet credited successfully");
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