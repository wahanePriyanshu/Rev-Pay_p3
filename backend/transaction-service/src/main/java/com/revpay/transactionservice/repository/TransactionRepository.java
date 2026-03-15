package com.revpay.transactionservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revpay.transactionservice.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderUserIdOrReceiverUserIdOrderByCreatedAtDescIdDesc(Long senderUserId, Long receiverUserId);
}