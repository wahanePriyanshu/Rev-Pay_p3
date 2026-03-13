package com.revpay.transactionservice.controller;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revpay.transactionservice.service.export.TransactionExportService;

@RestController
public class TransactionExportController {

    private final TransactionExportService exportService;

    public TransactionExportController(TransactionExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/api/transactions/export/csv")
    public ResponseEntity<InputStreamResource> exportCSV(Authentication authentication) {

        Long userId = extractUserId(authentication);

        ByteArrayInputStream stream = exportService.exportTransactionsToCSV(userId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/api/transactions/export/pdf")
    public ResponseEntity<InputStreamResource> exportPDF(Authentication authentication) {

        Long userId = extractUserId(authentication);

        ByteArrayInputStream stream = exportService.exportTransactionsToPDF(userId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(stream));
    }

    @SuppressWarnings("unchecked")
    private Long extractUserId(Authentication authentication) {

        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();

        Object value = details.get("userId");

        if (value instanceof Integer i) return i.longValue();
        if (value instanceof Long l) return l;

        return Long.valueOf(value.toString());
    }
}