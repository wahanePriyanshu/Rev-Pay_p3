package com.revpay.walletservice.dto.request;

import java.math.BigDecimal;

public class AddMoneyRequest {
    private BigDecimal amount;
    private String pin;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}