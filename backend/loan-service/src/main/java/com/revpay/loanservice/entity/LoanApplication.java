package com.revpay.loanservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.revpay.loanservice.enums.LoanStatus;
import com.revpay.loanservice.enums.LoanType;
import jakarta.persistence.*;

@Entity
@Table(name = "loan_applications", indexes = {
    @Index(name = "idx_loan_user", columnList = "user_id"),
    @Index(name = "idx_loan_created", columnList = "created_at DESC")
})
public class LoanApplication extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "loan_number", nullable = false, unique = true)
	private String loanNumber;

	@Column(name = "business_name", nullable = false)
	private String businessName;

	@Enumerated(EnumType.STRING)
	@Column(name = "loan_type", nullable = false)
	private LoanType loanType;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(name = "interest_rate", nullable = false, precision = 10, scale = 2)
	private BigDecimal interestRate;

	@Column(name = "tenure_months", nullable = false)
	private Integer tenureMonths;

	@Column(name = "monthly_emi", precision = 19, scale = 2)
	private BigDecimal monthlyEmi;

	@Column(length = 1000)
	private String purpose;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LoanStatus status;

	private LocalDateTime appliedAt;
	private LocalDateTime approvedAt;
	private LocalDateTime rejectedAt;
	private LocalDateTime closedAt;

	@OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LoanDocument> documents = new ArrayList<>();

	@OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LoanRepayment> repayments = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getLoanNumber() {
		return loanNumber;
	}

	public void setLoanNumber(String loanNumber) {
		this.loanNumber = loanNumber;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public LoanType getLoanType() {
		return loanType;
	}

	public void setLoanType(LoanType loanType) {
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

	public BigDecimal getMonthlyEmi() {
		return monthlyEmi;
	}

	public void setMonthlyEmi(BigDecimal monthlyEmi) {
		this.monthlyEmi = monthlyEmi;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public LoanStatus getStatus() {
		return status;
	}

	public void setStatus(LoanStatus status) {
		this.status = status;
	}

	public LocalDateTime getAppliedAt() {
		return appliedAt;
	}

	public void setAppliedAt(LocalDateTime appliedAt) {
		this.appliedAt = appliedAt;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}

	public LocalDateTime getRejectedAt() {
		return rejectedAt;
	}

	public void setRejectedAt(LocalDateTime rejectedAt) {
		this.rejectedAt = rejectedAt;
	}

	public LocalDateTime getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(LocalDateTime closedAt) {
		this.closedAt = closedAt;
	}

	public List<LoanDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<LoanDocument> documents) {
		this.documents = documents;
	}

	public List<LoanRepayment> getRepayments() {
		return repayments;
	}

	public void setRepayments(List<LoanRepayment> repayments) {
		this.repayments = repayments;
	}
}