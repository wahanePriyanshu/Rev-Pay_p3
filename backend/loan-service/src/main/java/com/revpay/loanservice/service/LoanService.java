package com.revpay.loanservice.service;

import java.util.List;

import com.revpay.loanservice.dto.request.CreateLoanRepaymentRequest;
import com.revpay.loanservice.dto.request.CreateLoanRequest;
import com.revpay.loanservice.dto.response.LoanAnalyticsResponse;
import com.revpay.loanservice.dto.response.LoanRepaymentResponse;
import com.revpay.loanservice.dto.response.LoanResponse;

public interface LoanService {
	LoanResponse createLoan(Long userId, CreateLoanRequest request);

	List<LoanResponse> getMyLoans(Long userId);

	LoanResponse getLoanById(Long userId, Long loanId);

	List<LoanRepaymentResponse> getRepayments(Long userId, Long loanId);

	LoanRepaymentResponse addRepayment(Long userId, Long loanId, CreateLoanRepaymentRequest request);

	LoanAnalyticsResponse getAnalytics(Long userId);
}