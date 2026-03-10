package com.revpay.userservice.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.revpay.userservice.dto.request.LoginRequest;
import com.revpay.userservice.dto.request.RegisterRequest;
import com.revpay.userservice.dto.response.AuthResponse;
import com.revpay.userservice.entity.User;
import com.revpay.userservice.enums.AccountType;
import com.revpay.userservice.enums.UserStatus;
import com.revpay.userservice.repository.UserRepository;
import com.revpay.userservice.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAccountType(AccountType.PERSONAL);
        user.setStatus(UserStatus.ACTIVE);
        user.setIsVerified(true);

        User savedUser = userRepository.save(user);

        return new AuthResponse("dummy-token", savedUser.getId(), "USER");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmailOrPhone())
                .orElseGet(() -> userRepository.findByPhone(request.getEmailOrPhone())
                        .orElseThrow(() -> new RuntimeException("Invalid email/phone or password")));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        if (!passwordMatches) {
            throw new RuntimeException("Invalid email/phone or password");
        }

        return new AuthResponse("dummy-token", user.getId(), "USER");
    }
}