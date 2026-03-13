package com.revpay.invoiceservice.service;

import java.util.List;

import com.revpay.invoiceservice.dto.request.CreateInvoiceRequest;
import com.revpay.invoiceservice.dto.response.InvoiceResponse;

public interface InvoiceService {
    InvoiceResponse createInvoice(Long ownerUserId, CreateInvoiceRequest request);
    List<InvoiceResponse> getMyInvoices(Long ownerUserId);
    InvoiceResponse getInvoiceById(Long ownerUserId, Long invoiceId);
    InvoiceResponse sendInvoice(Long ownerUserId, Long invoiceId);
    InvoiceResponse payInvoice(Long ownerUserId, Long invoiceId);
    InvoiceResponse cancelInvoice(Long ownerUserId, Long invoiceId);
}