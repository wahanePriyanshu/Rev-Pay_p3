package com.revpay.invoiceservice.service;

import java.util.List;

import com.revpay.invoiceservice.dto.request.CreateCustomerRequest;
import com.revpay.invoiceservice.dto.request.UpdateCustomerRequest;
import com.revpay.invoiceservice.dto.response.CustomerResponse;

public interface CustomerService {
    CustomerResponse createCustomer(Long ownerUserId, CreateCustomerRequest request);
    List<CustomerResponse> getMyCustomers(Long ownerUserId);
    CustomerResponse getCustomerById(Long ownerUserId, Long customerId);
    CustomerResponse updateCustomer(Long ownerUserId, Long customerId, UpdateCustomerRequest request);
    void deleteCustomer(Long ownerUserId, Long customerId);
}