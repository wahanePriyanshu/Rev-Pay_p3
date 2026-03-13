package com.revpay.invoiceservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.revpay.invoiceservice.dto.request.CreateCustomerRequest;
import com.revpay.invoiceservice.dto.request.UpdateCustomerRequest;
import com.revpay.invoiceservice.dto.response.CustomerResponse;
import com.revpay.invoiceservice.service.CustomerService;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/api/customers")
    public CustomerResponse createCustomer(Authentication authentication,
                                           @RequestBody CreateCustomerRequest request) {
        Long userId = extractUserId(authentication);
        return customerService.createCustomer(userId, request);
    }

    @GetMapping("/api/customers")
    public List<CustomerResponse> getCustomers(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return customerService.getMyCustomers(userId);
    }

    @GetMapping("/api/customers/{id}")
    public CustomerResponse getCustomerById(Authentication authentication,
                                            @PathVariable Long id) {
        Long userId = extractUserId(authentication);
        return customerService.getCustomerById(userId, id);
    }

    @PutMapping("/api/customers/{id}")
    public CustomerResponse updateCustomer(Authentication authentication,
                                           @PathVariable Long id,
                                           @RequestBody UpdateCustomerRequest request) {
        Long userId = extractUserId(authentication);
        return customerService.updateCustomer(userId, id, request);
    }

    @DeleteMapping("/api/customers/{id}")
    public Map<String, String> deleteCustomer(Authentication authentication,
                                              @PathVariable Long id) {
        Long userId = extractUserId(authentication);
        customerService.deleteCustomer(userId, id);
        return Map.of("message", "Customer deleted successfully");
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