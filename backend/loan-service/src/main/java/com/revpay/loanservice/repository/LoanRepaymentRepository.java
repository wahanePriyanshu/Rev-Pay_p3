package com.revpay.loanservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revpay.loanservice.entity.LoanRepayment;

public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {
    List<LoanRepayment> findByLoanIdOrderByPaymentDateDesc(Long loanId);
}