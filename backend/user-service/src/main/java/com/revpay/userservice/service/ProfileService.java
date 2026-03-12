package com.revpay.userservice.service;

import com.revpay.userservice.dto.request.ChangePasswordRequest;
import com.revpay.userservice.dto.request.ChangePinRequest;
import com.revpay.userservice.dto.request.SetPinRequest;
import com.revpay.userservice.dto.request.UpdateProfileRequest;
import com.revpay.userservice.dto.response.ProfileResponse;

public interface ProfileService {
    ProfileResponse getMyProfile(String email);
    ProfileResponse updateMyProfile(String email, UpdateProfileRequest request);
    String setPin(String email, SetPinRequest request);
    String changePin(String email, ChangePinRequest request);
    String changePassword(String email, ChangePasswordRequest request);
}