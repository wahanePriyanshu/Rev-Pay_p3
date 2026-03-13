package com.revpay.loanservice.dto.request;

import java.math.BigDecimal;
import java.util.List;

public class CreateLoanRequest {
	private String businessName;
	private String loanType;
	private BigDecimal amount;
	private BigDecimal interestRate;
	private Integer tenureMonths;
	private String purpose;
	private List<LoanDocumentRequest> documents;

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public Integer getTenureMonths() {
		return tenureMonths;
	}

	public void setTenureMonths(Integer tenureMonths) {
		this.tenureMonths = tenureMonths;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public List<LoanDocumentRequest> getDocuments() {
		return documents;
	}

	public void setDocuments(List<LoanDocumentRequest> documents) {
		this.documents = documents;
	}
}