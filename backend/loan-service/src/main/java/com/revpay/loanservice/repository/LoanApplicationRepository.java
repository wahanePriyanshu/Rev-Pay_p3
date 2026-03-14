package com.revpay.loanservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.revpay.loanservice.entity.LoanApplication;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    
    @EntityGraph(attributePaths = {"documents", "repayments"})
    List<LoanApplication> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"documents", "repayments"})
    Optional<LoanApplication> findByIdAndUserId(Long id, Long userId);
}