package com.revpay.notificationservice.dto.response;

public class NotificationPreferenceResponse {
	private Long id;
	private Long userId;
	private Boolean transactionAlerts;
	private Boolean requestAlerts;
	private Boolean invoiceAlerts;
	private Boolean loanAlerts;
	private Boolean lowBalanceAlerts;
	private Boolean systemAlerts;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getTransactionAlerts() {
		return transactionAlerts;
	}

	public void setTransactionAlerts(Boolean transactionAlerts) {
		this.transactionAlerts = transactionAlerts;
	}

	public Boolean getRequestAlerts() {
		return requestAlerts;
	}

	public void setRequestAlerts(Boolean requestAlerts) {
		this.requestAlerts = requestAlerts;
	}

	public Boolean getInvoiceAlerts() {
		return invoiceAlerts;
	}

	public void setInvoiceAlerts(Boolean invoiceAlerts) {
		this.invoiceAlerts = invoiceAlerts;
	}

	public Boolean getLoanAlerts() {
		return loanAlerts;
	}

	public void setLoanAlerts(Boolean loanAlerts) {
		this.loanAlerts = loanAlerts;
	}

	public Boolean getLowBalanceAlerts() {
		return lowBalanceAlerts;
	}

	public void setLowBalanceAlerts(Boolean lowBalanceAlerts) {
		this.lowBalanceAlerts = lowBalanceAlerts;
	}

	public Boolean getSystemAlerts() {
		return systemAlerts;
	}

	public void setSystemAlerts(Boolean systemAlerts) {
		this.systemAlerts = systemAlerts;
	}
}