package com.revpay.transactionservice.dto.request;

import java.math.BigDecimal;

public class SendMoneyRequest {
    private Long receiverUserId;
    private String to;          // email / phone lookup field from frontend
    private String note;        // alias for description from frontend
    private BigDecimal amount;
    private String description;
    private String pin;

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}