package com.revpay.transactionservice.client;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wallet-service", url = "http://localhost:8082")
public interface WalletClient {

    @PostMapping("/api/wallet/internal/debit")
    void debit(@RequestBody WalletOperationRequest request);

    @PostMapping("/api/wallet/internal/credit")
    void credit(@RequestBody WalletOperationRequest request);

    class WalletOperationRequest {
        private Long userId;
        private BigDecimal amount;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}