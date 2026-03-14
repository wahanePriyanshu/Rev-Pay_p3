package com.revpay.invoiceservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.revpay.invoiceservice.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    @EntityGraph(attributePaths = {"customer", "items"})
    List<Invoice> findByOwnerUserIdOrderByCreatedAtDesc(Long ownerUserId);

    @EntityGraph(attributePaths = {"customer", "items"})
    Optional<Invoice> findByIdAndOwnerUserId(Long id, Long ownerUserId);
}