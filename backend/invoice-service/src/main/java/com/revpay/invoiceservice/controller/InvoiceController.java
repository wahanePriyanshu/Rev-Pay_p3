package com.revpay.invoiceservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.revpay.invoiceservice.dto.request.CreateInvoiceRequest;
import com.revpay.invoiceservice.dto.response.InvoiceResponse;
import com.revpay.invoiceservice.service.InvoiceService;

@RestController
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/api/invoices")
    public InvoiceResponse createInvoice(Authentication authentication,
                                         @RequestBody CreateInvoiceRequest request) {
        Long userId = extractUserId(authentication);
        return invoiceService.createInvoice(userId, request);
    }

    @GetMapping("/api/invoices")
    public List<InvoiceResponse> getInvoices(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return invoiceService.getMyInvoices(userId);
    }

    @GetMapping("/api/invoices/{id}")
    public InvoiceResponse getInvoiceById(Authentication authentication,
                                          @PathVariable Long id) {
        Long userId = extractUserId(authentication);
        return invoiceService.getInvoiceById(userId, id);
    }

    @PutMapping("/api/invoices/{id}/send")
    public InvoiceResponse sendInvoice(Authentication authentication,
                                       @PathVariable Long id) {
        Long userId = extractUserId(authentication);
        return invoiceService.sendInvoice(userId, id);
    }

    @PutMapping("/api/invoices/{id}/pay")
    public InvoiceResponse payInvoice(Authentication authentication,
                                      @PathVariable Long id) {
        Long userId = extractUserId(authentication);
        return invoiceService.payInvoice(userId, id);
    }

    @PutMapping("/api/invoices/{id}/cancel")
    public InvoiceResponse cancelInvoice(Authentication authentication,
                                         @PathVariable Long id) {
        Long userId = extractUserId(authentication);
        return invoiceService.cancelInvoice(userId, id);
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