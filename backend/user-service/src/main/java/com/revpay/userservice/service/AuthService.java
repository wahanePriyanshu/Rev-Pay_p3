package com.revpay.userservice.service;

import com.revpay.userservice.dto.request.LoginRequest;
import com.revpay.userservice.dto.request.RegisterRequest;
import com.revpay.userservice.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}