package com.revpay.transactionservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revpay.transactionservice.entity.MoneyRequest;

public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {
    List<MoneyRequest> findByRequesterUserIdOrderByCreatedAtDescIdDesc(Long requesterUserId);
    List<MoneyRequest> findByPayerUserIdOrderByCreatedAtDescIdDesc(Long payerUserId);
}