package com.revpay.walletservice.dto.response;

import java.math.BigDecimal;

public class WalletBalanceResponse {
    private Long walletId;
    private BigDecimal balance;
    private Boolean hasPin;

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Boolean getHasPin() {
        return hasPin;
    }

    public void setHasPin(Boolean hasPin) {
        this.hasPin = hasPin;
    }
}