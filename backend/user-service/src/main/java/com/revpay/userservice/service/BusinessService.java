package com.revpay.userservice.service;

import com.revpay.userservice.dto.request.BusinessRegisterRequest;
import com.revpay.userservice.dto.response.BusinessProfileResponse;

public interface BusinessService {
    BusinessProfileResponse registerBusiness(String email, BusinessRegisterRequest request);
    BusinessProfileResponse getMyBusiness(String email);
}