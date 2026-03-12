package com.revpay.userservice.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.revpay.userservice.dto.request.BusinessRegisterRequest;
import com.revpay.userservice.dto.response.BusinessProfileResponse;
import com.revpay.userservice.service.BusinessService;

@RestController
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @PostMapping("/api/business/register")
    public BusinessProfileResponse registerBusiness(Principal principal,
                                                    @RequestBody BusinessRegisterRequest request) {
        return businessService.registerBusiness(principal.getName(), request);
    }

    @GetMapping("/api/business/me")
    public BusinessProfileResponse getMyBusiness(Principal principal) {
        return businessService.getMyBusiness(principal.getName());
    }
}