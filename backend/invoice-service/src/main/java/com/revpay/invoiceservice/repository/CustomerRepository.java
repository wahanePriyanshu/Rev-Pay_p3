package com.revpay.invoiceservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.revpay.invoiceservice.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    List<Customer> findByOwnerUserIdOrderByCreatedAtDescIdDesc(Long ownerUserId);

    Optional<Customer> findByIdAndOwnerUserId(Long id, Long ownerUserId);
}