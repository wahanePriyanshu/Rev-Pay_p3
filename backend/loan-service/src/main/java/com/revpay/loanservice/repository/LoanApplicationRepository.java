package com.revpay.loanservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revpay.loanservice.entity.LoanApplication;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<LoanApplication> findByIdAndUserId(Long id, Long userId);
}