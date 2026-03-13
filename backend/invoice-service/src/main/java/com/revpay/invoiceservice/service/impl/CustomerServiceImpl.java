package com.revpay.invoiceservice.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.revpay.invoiceservice.dto.request.CreateCustomerRequest;
import com.revpay.invoiceservice.dto.request.UpdateCustomerRequest;
import com.revpay.invoiceservice.dto.response.CustomerResponse;
import com.revpay.invoiceservice.entity.Customer;
import com.revpay.invoiceservice.enums.CustomerStatus;
import com.revpay.invoiceservice.repository.CustomerRepository;
import com.revpay.invoiceservice.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse createCustomer(Long ownerUserId, CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setOwnerUserId(ownerUserId);
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setStatus(CustomerStatus.ACTIVE);

        Customer saved = customerRepository.save(customer);
        return mapToResponse(saved);
    }

    @Override
    public List<CustomerResponse> getMyCustomers(Long ownerUserId) {
        return customerRepository.findByOwnerUserIdOrderByCreatedAtDesc(ownerUserId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomerById(Long ownerUserId, Long customerId) {
        Customer customer = customerRepository.findByIdAndOwnerUserId(customerId, ownerUserId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(Long ownerUserId, Long customerId, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findByIdAndOwnerUserId(customerId, ownerUserId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (request.getName() != null) customer.setName(request.getName());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getPhone() != null) customer.setPhone(request.getPhone());
        if (request.getAddress() != null) customer.setAddress(request.getAddress());

        Customer saved = customerRepository.save(customer);
        return mapToResponse(saved);
    }

    @Override
    public void deleteCustomer(Long ownerUserId, Long customerId) {
        Customer customer = customerRepository.findByIdAndOwnerUserId(customerId, ownerUserId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerRepository.delete(customer);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setOwnerUserId(customer.getOwnerUserId());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setAddress(customer.getAddress());
        response.setStatus(customer.getStatus().name());
        return response;
    }
}