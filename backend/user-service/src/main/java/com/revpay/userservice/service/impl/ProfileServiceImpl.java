package com.revpay.userservice.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.revpay.userservice.dto.request.ChangePasswordRequest;
import com.revpay.userservice.dto.request.ChangePinRequest;
import com.revpay.userservice.dto.request.SetPinRequest;
import com.revpay.userservice.dto.request.UpdateProfileRequest;
import com.revpay.userservice.dto.response.ProfileResponse;
import com.revpay.userservice.dto.response.UserDto;
import java.util.stream.Collectors;
import java.util.List;
import com.revpay.userservice.entity.User;
import com.revpay.userservice.repository.UserRepository;
import com.revpay.userservice.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileServiceImpl(UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ProfileResponse getMyProfile(String email) {
        User user = findUserByEmail(email);
        return mapToProfileResponse(user);
    }

    @Override
    public List<UserDto> getAllUsers(String currentEmail) {
        return userRepository.findAll().stream()
                .filter(u -> !u.getEmail().equals(currentEmail))
                .map(u -> new UserDto(u.getId(), u.getFullName(), u.getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return new UserDto(user.getId(), user.getFullName(), user.getEmail());
    }

    @Override
    public ProfileResponse updateMyProfile(String email, UpdateProfileRequest request) {
        User user = findUserByEmail(email);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            user.setPhone(request.getPhone());
        }

        User savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }

    @Override
    public String setPin(String email, SetPinRequest request) {
        User user = findUserByEmail(email);

        if (user.getPinHash() != null && !user.getPinHash().isBlank()) {
            throw new RuntimeException("PIN already set");
        }

        user.setPinHash(passwordEncoder.encode(request.getPin()));
        userRepository.save(user);

        return "PIN set successfully";
    }

    @Override
    public String changePin(String email, ChangePinRequest request) {
        User user = findUserByEmail(email);

        if (user.getPinHash() == null || user.getPinHash().isBlank()) {
            throw new RuntimeException("PIN not set");
        }

        if (!passwordEncoder.matches(request.getOldPin(), user.getPinHash())) {
            throw new RuntimeException("Old PIN is incorrect");
        }

        user.setPinHash(passwordEncoder.encode(request.getNewPin()));
        userRepository.save(user);

        return "PIN changed successfully";
    }

    @Override
    public String changePassword(String email, ChangePasswordRequest request) {
        User user = findUserByEmail(email);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Password changed successfully";
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ProfileResponse mapToProfileResponse(User user) {
        ProfileResponse response = new ProfileResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAccountType(user.getAccountType().name());
        response.setStatus(user.getStatus().name());
        response.setIsVerified(user.getIsVerified());
        response.setHasPin(user.getPinHash() != null && !user.getPinHash().isBlank());
        return response;
    }
}