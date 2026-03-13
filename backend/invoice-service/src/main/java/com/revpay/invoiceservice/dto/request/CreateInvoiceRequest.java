package com.revpay.invoiceservice.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CreateInvoiceRequest {
	private Long customerId;
	private LocalDate issueDate;
	private LocalDate dueDate;
	private BigDecimal taxAmount;
	private String notes;
	private List<CreateInvoiceItemRequest> items;

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<CreateInvoiceItemRequest> getItems() {
		return items;
	}

	public void setItems(List<CreateInvoiceItemRequest> items) {
		this.items = items;
	}
}