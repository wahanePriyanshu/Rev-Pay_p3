package com.revpay.userservice.service.impl;

import org.springframework.stereotype.Service;

import com.revpay.userservice.dto.request.BusinessRegisterRequest;
import com.revpay.userservice.dto.response.BusinessProfileResponse;
import com.revpay.userservice.entity.BusinessProfile;
import com.revpay.userservice.entity.User;
import com.revpay.userservice.enums.AccountType;
import com.revpay.userservice.repository.BusinessProfileRepository;
import com.revpay.userservice.repository.UserRepository;
import com.revpay.userservice.service.BusinessService;

@Service
public class BusinessServiceImpl implements BusinessService {

    private final BusinessProfileRepository businessProfileRepository;
    private final UserRepository userRepository;

    public BusinessServiceImpl(BusinessProfileRepository businessProfileRepository,
                               UserRepository userRepository) {
        this.businessProfileRepository = businessProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BusinessProfileResponse registerBusiness(String email, BusinessRegisterRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (businessProfileRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("Business profile already exists");
        }

        user.setAccountType(AccountType.BUSINESS);
        userRepository.save(user);

        BusinessProfile profile = new BusinessProfile();
        profile.setUserId(user.getId());
        profile.setBusinessName(request.getBusinessName());
        profile.setBusinessType(request.getBusinessType());
        profile.setTaxId(request.getTaxId());
        profile.setAddress(request.getAddress());
        profile.setWebsite(request.getWebsite());

        BusinessProfile saved = businessProfileRepository.save(profile);

        return mapToResponse(saved);
    }

    @Override
    public BusinessProfileResponse getMyBusiness(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BusinessProfile profile = businessProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Business profile not found"));

        return mapToResponse(profile);
    }

    private BusinessProfileResponse mapToResponse(BusinessProfile profile) {
        BusinessProfileResponse response = new BusinessProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUserId());
        response.setBusinessName(profile.getBusinessName());
        response.setBusinessType(profile.getBusinessType());
        response.setTaxId(profile.getTaxId());
        response.setAddress(profile.getAddress());
        response.setWebsite(profile.getWebsite());
        return response;
    }
}