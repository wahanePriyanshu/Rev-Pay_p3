package com.revpay.loanservice.dto.request;

import java.math.BigDecimal;

public class CreateLoanRepaymentRequest {
	private BigDecimal amount;
	private String notes;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}