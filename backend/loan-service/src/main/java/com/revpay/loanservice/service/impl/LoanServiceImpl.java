package com.revpay.loanservice.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.revpay.loanservice.dto.request.CreateLoanRepaymentRequest;
import com.revpay.loanservice.dto.request.CreateLoanRequest;
import com.revpay.loanservice.dto.request.LoanDocumentRequest;
import com.revpay.loanservice.dto.response.*;
import com.revpay.loanservice.entity.*;
import com.revpay.loanservice.enums.LoanStatus;
import com.revpay.loanservice.enums.LoanType;
import com.revpay.loanservice.repository.LoanApplicationRepository;
import com.revpay.loanservice.repository.LoanRepaymentRepository;
import com.revpay.loanservice.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

	private final LoanApplicationRepository loanRepository;
	private final LoanRepaymentRepository repaymentRepository;

	public LoanServiceImpl(LoanApplicationRepository loanRepository, LoanRepaymentRepository repaymentRepository) {
		this.loanRepository = loanRepository;
		this.repaymentRepository = repaymentRepository;
	}

	@Override
	public LoanResponse createLoan(Long userId, CreateLoanRequest request) {
		LoanApplication loan = new LoanApplication();
		loan.setUserId(userId);
		loan.setLoanNumber(generateLoanNumber());
		loan.setBusinessName(request.getBusinessName());
		loan.setLoanType(LoanType.valueOf(request.getLoanType()));
		loan.setAmount(request.getAmount());
		loan.setInterestRate(request.getInterestRate());
		loan.setTenureMonths(request.getTenureMonths());
		loan.setMonthlyEmi(calculateEmi(request.getAmount(), request.getInterestRate(), request.getTenureMonths()));
		loan.setPurpose(request.getPurpose());
		loan.setStatus(LoanStatus.PENDING);
		loan.setAppliedAt(LocalDateTime.now());

		if (request.getDocuments() != null) {
			for (LoanDocumentRequest docRequest : request.getDocuments()) {
				LoanDocument document = new LoanDocument();
				document.setLoan(loan);
				document.setDocumentName(docRequest.getDocumentName());
				document.setDocumentType(docRequest.getDocumentType());
				document.setFileUrl(docRequest.getFileUrl());
				document.setUploadedAt(LocalDateTime.now());
				loan.getDocuments().add(document);
			}
		}

		LoanApplication saved = loanRepository.save(loan);
		return mapToLoanResponse(saved);
	}

	@Override
	public List<LoanResponse> getMyLoans(Long userId) {
		return loanRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId).stream().map(this::mapToLoanResponse).toList();
	}

	@Override
	public LoanResponse getLoanById(Long userId, Long loanId) {
		LoanApplication loan = loanRepository.findByIdAndUserId(loanId, userId)
				.orElseThrow(() -> new RuntimeException("Loan not found"));
		return mapToLoanResponse(loan);
	}

	@Override
	public List<LoanRepaymentResponse> getRepayments(Long userId, Long loanId) {
		LoanApplication loan = loanRepository.findByIdAndUserId(loanId, userId)
				.orElseThrow(() -> new RuntimeException("Loan not found"));

		return repaymentRepository.findByLoanIdOrderByPaymentDateDesc(loan.getId()).stream()
				.map(this::mapToRepaymentResponse).toList();
	}

	@Override
	public LoanRepaymentResponse addRepayment(Long userId, Long loanId, CreateLoanRepaymentRequest request) {
		LoanApplication loan = loanRepository.findByIdAndUserId(loanId, userId)
				.orElseThrow(() -> new RuntimeException("Loan not found"));

		if (loan.getStatus() == LoanStatus.REJECTED || loan.getStatus() == LoanStatus.CLOSED) {
			throw new RuntimeException("Repayment cannot be added for this loan");
		}

		LoanRepayment repayment = new LoanRepayment();
		repayment.setLoan(loan);
		repayment.setAmount(request.getAmount());
		repayment.setPaymentDate(LocalDateTime.now());
		repayment.setPaymentReference(generatePaymentReference());
		repayment.setNotes(request.getNotes());

		LoanRepayment saved = repaymentRepository.save(repayment);
		return mapToRepaymentResponse(saved);
	}

	@Override
	public LoanAnalyticsResponse getAnalytics(Long userId) {
		List<LoanApplication> loans = loanRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId);

		BigDecimal totalBorrowed = loans.stream().map(LoanApplication::getAmount).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal totalRepaid = loans.stream().flatMap(loan -> loan.getRepayments().stream())
				.map(LoanRepayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		LoanAnalyticsResponse response = new LoanAnalyticsResponse();
		response.setTotalLoans((long) loans.size());
		response.setTotalBorrowed(totalBorrowed);
		response.setTotalRepaid(totalRepaid);
		response.setOutstandingAmount(totalBorrowed.subtract(totalRepaid));
		return response;
	}

	private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, Integer tenureMonths) {
		if (principal == null || annualRate == null || tenureMonths == null || tenureMonths <= 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100.0), 10, java.math.RoundingMode.HALF_UP);
		if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
			return principal.divide(BigDecimal.valueOf(tenureMonths), 2, java.math.RoundingMode.HALF_UP);
		}

		double p = principal.doubleValue();
		double r = monthlyRate.doubleValue();
		int n = tenureMonths;

		double emi = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
		return BigDecimal.valueOf(emi).setScale(2, java.math.RoundingMode.HALF_UP);
	}

	private String generateLoanNumber() {
		return "LOAN" + (100000 + new Random().nextInt(900000));
	}

	private String generatePaymentReference() {
		return "PAY" + (100000 + new Random().nextInt(900000));
	}

	private LoanResponse mapToLoanResponse(LoanApplication loan) {
		LoanResponse response = new LoanResponse();
		response.setId(loan.getId());
		response.setUserId(loan.getUserId());
		response.setLoanNumber(loan.getLoanNumber());
		response.setBusinessName(loan.getBusinessName());
		response.setLoanType(loan.getLoanType().name());
		response.setAmount(loan.getAmount());
		response.setInterestRate(loan.getInterestRate());
		response.setTenureMonths(loan.getTenureMonths());
		response.setMonthlyEmi(loan.getMonthlyEmi());
		response.setPurpose(loan.getPurpose());
		response.setStatus(loan.getStatus().name());
		response.setAppliedAt(loan.getAppliedAt());
		response.setApprovedAt(loan.getApprovedAt());
		response.setRejectedAt(loan.getRejectedAt());
		response.setClosedAt(loan.getClosedAt());

		List<LoanDocumentResponse> docs = loan.getDocuments().stream().map(doc -> {
			LoanDocumentResponse d = new LoanDocumentResponse();
			d.setId(doc.getId());
			d.setDocumentName(doc.getDocumentName());
			d.setDocumentType(doc.getDocumentType());
			d.setFileUrl(doc.getFileUrl());
			return d;
		}).toList();

		response.setDocuments(docs);
		return response;
	}

	private LoanRepaymentResponse mapToRepaymentResponse(LoanRepayment repayment) {
		LoanRepaymentResponse response = new LoanRepaymentResponse();
		response.setId(repayment.getId());
		response.setAmount(repayment.getAmount());
		response.setPaymentDate(repayment.getPaymentDate());
		response.setPaymentReference(repayment.getPaymentReference());
		response.setNotes(repayment.getNotes());
		return response;
	}
}