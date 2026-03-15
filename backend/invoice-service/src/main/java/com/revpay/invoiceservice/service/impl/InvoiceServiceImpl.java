package com.revpay.invoiceservice.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.revpay.invoiceservice.dto.request.CreateInvoiceItemRequest;
import com.revpay.invoiceservice.dto.request.CreateInvoiceRequest;
import com.revpay.invoiceservice.dto.response.InvoiceItemResponse;
import com.revpay.invoiceservice.dto.response.InvoiceResponse;
import com.revpay.invoiceservice.entity.Customer;
import com.revpay.invoiceservice.entity.Invoice;
import com.revpay.invoiceservice.entity.InvoiceItem;
import com.revpay.invoiceservice.enums.InvoiceStatus;
import com.revpay.invoiceservice.repository.CustomerRepository;
import com.revpay.invoiceservice.repository.InvoiceRepository;
import com.revpay.invoiceservice.service.InvoiceService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public InvoiceResponse createInvoice(Long ownerUserId, CreateInvoiceRequest request) {
        Customer customer = customerRepository.findByIdAndOwnerUserId(request.getCustomerId(), ownerUserId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setOwnerUserId(ownerUserId);
        invoice.setCustomer(customer);
        invoice.setIssueDate(request.getIssueDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setNotes(request.getNotes());
        invoice.setStatus(InvoiceStatus.DRAFT);

        List<InvoiceItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CreateInvoiceItemRequest itemRequest : request.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setDescription(itemRequest.getDescription());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());

            BigDecimal lineTotal = itemRequest.getQuantity().multiply(itemRequest.getUnitPrice());
            item.setLineTotal(lineTotal);

            subtotal = subtotal.add(lineTotal);
            items.add(item);
        }

        BigDecimal taxAmount = request.getTaxAmount() == null ? BigDecimal.ZERO : request.getTaxAmount();
        BigDecimal totalAmount = subtotal.add(taxAmount);

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setItems(items);

        Invoice saved = invoiceRepository.save(invoice);
        return mapToResponse(saved);
    }

    @Override
    public List<InvoiceResponse> getMyInvoices(Long ownerUserId) {
        return invoiceRepository.findByOwnerUserIdOrderByCreatedAtDescIdDesc(ownerUserId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public InvoiceResponse getInvoiceById(Long ownerUserId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findByIdAndOwnerUserId(invoiceId, ownerUserId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return mapToResponse(invoice);
    }

    @Override
    public InvoiceResponse sendInvoice(Long ownerUserId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findByIdAndOwnerUserId(invoiceId, ownerUserId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new RuntimeException("Only draft invoices can be sent");
        }

        invoice.setStatus(InvoiceStatus.SENT);
        invoice.setSentAt(LocalDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);
        return mapToResponse(saved);
    }

    @Override
    public InvoiceResponse payInvoice(Long ownerUserId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findByIdAndOwnerUserId(invoiceId, ownerUserId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.SENT) {
            throw new RuntimeException("Only sent invoices can be marked paid");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);
        return mapToResponse(saved);
    }

    @Override
    public InvoiceResponse cancelInvoice(Long ownerUserId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findByIdAndOwnerUserId(invoiceId, ownerUserId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Paid invoices cannot be cancelled");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice.setCancelledAt(LocalDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);
        return mapToResponse(saved);
    }

    private String generateInvoiceNumber() {
        return "INV" + (100000 + new Random().nextInt(900000));
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setOwnerUserId(invoice.getOwnerUserId());
        response.setCustomerId(invoice.getCustomer().getId());
        response.setCustomerName(invoice.getCustomer().getName());
        response.setIssueDate(invoice.getIssueDate());
        response.setDueDate(invoice.getDueDate());
        response.setSubtotal(invoice.getSubtotal());
        response.setTaxAmount(invoice.getTaxAmount());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setStatus(invoice.getStatus().name());
        response.setNotes(invoice.getNotes());
        response.setSentAt(invoice.getSentAt());
        response.setPaidAt(invoice.getPaidAt());
        response.setCancelledAt(invoice.getCancelledAt());

        List<InvoiceItemResponse> itemResponses = invoice.getItems().stream().map(item -> {
            InvoiceItemResponse itemResponse = new InvoiceItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setDescription(item.getDescription());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setLineTotal(item.getLineTotal());
            return itemResponse;
        }).toList();

        response.setItems(itemResponses);
        return response;
    }
}