package com.revpay.invoiceservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revpay.invoiceservice.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByOwnerUserIdOrderByCreatedAtDesc(Long ownerUserId);
    Optional<Invoice> findByIdAndOwnerUserId(Long id, Long ownerUserId);
}