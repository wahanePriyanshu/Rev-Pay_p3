package com.revpay.notificationservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false, unique = true)
	private Long userId;

	@Column(name = "transaction_alerts", nullable = false)
	private Boolean transactionAlerts = true;

	@Column(name = "request_alerts", nullable = false)
	private Boolean requestAlerts = true;

	@Column(name = "invoice_alerts", nullable = false)
	private Boolean invoiceAlerts = true;

	@Column(name = "loan_alerts", nullable = false)
	private Boolean loanAlerts = true;

	@Column(name = "low_balance_alerts", nullable = false)
	private Boolean lowBalanceAlerts = true;

	@Column(name = "system_alerts", nullable = false)
	private Boolean systemAlerts = true;

	public Long getId() {
		return id;
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