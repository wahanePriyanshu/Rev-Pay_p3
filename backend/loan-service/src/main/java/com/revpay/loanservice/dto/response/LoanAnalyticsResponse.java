package com.revpay.loanservice.dto.response;

import java.math.BigDecimal;

public class LoanAnalyticsResponse {
	private Long totalLoans;
	private BigDecimal totalBorrowed;
	private BigDecimal totalRepaid;
	private BigDecimal outstandingAmount;

	public Long getTotalLoans() {
		return totalLoans;
	}

	public void setTotalLoans(Long totalLoans) {
		this.totalLoans = totalLoans;
	}

	public BigDecimal getTotalBorrowed() {
		return totalBorrowed;
	}

	public void setTotalBorrowed(BigDecimal totalBorrowed) {
		this.totalBorrowed = totalBorrowed;
	}

	public BigDecimal getTotalRepaid() {
		return totalRepaid;
	}

	public void setTotalRepaid(BigDecimal totalRepaid) {
		this.totalRepaid = totalRepaid;
	}

	public BigDecimal getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(BigDecimal outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}
}