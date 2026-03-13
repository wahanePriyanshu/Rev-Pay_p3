package com.revpay.loanservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.revpay.loanservice.dto.request.CreateLoanRepaymentRequest;
import com.revpay.loanservice.dto.request.CreateLoanRequest;
import com.revpay.loanservice.dto.response.LoanAnalyticsResponse;
import com.revpay.loanservice.dto.response.LoanRepaymentResponse;
import com.revpay.loanservice.dto.response.LoanResponse;
import com.revpay.loanservice.service.LoanService;

@RestController
public class LoanController {

	private final LoanService loanService;

	public LoanController(LoanService loanService) {
		this.loanService = loanService;
	}

	@GetMapping("/api/loans")
	public List<LoanResponse> getLoans(Authentication authentication) {
		Long userId = extractUserId(authentication);
		return loanService.getMyLoans(userId);
	}

	@PostMapping("/api/loans")
	public LoanResponse createLoan(Authentication authentication, @RequestBody CreateLoanRequest request) {
		Long userId = extractUserId(authentication);
		return loanService.createLoan(userId, request);
	}

	@GetMapping("/api/loans/{id}")
	public LoanResponse getLoanById(Authentication authentication, @PathVariable Long id) {
		Long userId = extractUserId(authentication);
		return loanService.getLoanById(userId, id);
	}

	@GetMapping("/api/loans/{id}/repayments")
	public List<LoanRepaymentResponse> getRepayments(Authentication authentication, @PathVariable Long id) {
		Long userId = extractUserId(authentication);
		return loanService.getRepayments(userId, id);
	}

	@PostMapping("/api/loans/{id}/repayments")
	public LoanRepaymentResponse addRepayment(Authentication authentication, @PathVariable Long id,
			@RequestBody CreateLoanRepaymentRequest request) {
		Long userId = extractUserId(authentication);
		return loanService.addRepayment(userId, id, request);
	}

	@GetMapping("/api/loans/analytics")
	public LoanAnalyticsResponse getAnalytics(Authentication authentication) {
		Long userId = extractUserId(authentication);
		return loanService.getAnalytics(userId);
	}

	@SuppressWarnings("unchecked")
	private Long extractUserId(Authentication authentication) {
		Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
		Object value = details.get("userId");

		if (value instanceof Integer i)
			return i.longValue();
		if (value instanceof Long l)
			return l;
		return Long.valueOf(value.toString());
	}
}