package com.revpay.userservice.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revpay.userservice.dto.request.ChangePasswordRequest;
import com.revpay.userservice.dto.request.ChangePinRequest;
import com.revpay.userservice.dto.request.SetPinRequest;
import com.revpay.userservice.dto.request.UpdateProfileRequest;
import com.revpay.userservice.dto.response.ProfileResponse;
import com.revpay.userservice.dto.response.UserDto;
import com.revpay.userservice.service.ProfileService;

@RestController
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/api/profile")
    public ProfileResponse getMyProfile(Principal principal) {
        return profileService.getMyProfile(principal.getName());
    }

    @GetMapping("/api/users")
    public List<UserDto> getAllUsers(Principal principal) {
        return profileService.getAllUsers(principal.getName());
    }

    @GetMapping("/api/users/by-email")
    public UserDto getUserByEmail(@RequestParam("email") String email) {
        return profileService.getUserByEmail(email);
    }

    @PutMapping("/api/profile")
    public ProfileResponse updateMyProfile(Principal principal,
                                           @RequestBody UpdateProfileRequest request) {
        return profileService.updateMyProfile(principal.getName(), request);
    }

    @PostMapping("/api/profile/set-pin")
    public String setPin(Principal principal,
                         @RequestBody SetPinRequest request) {
        return profileService.setPin(principal.getName(), request);
    }

    @PostMapping("/api/profile/change-pin")
    public String changePin(Principal principal,
                            @RequestBody ChangePinRequest request) {
        return profileService.changePin(principal.getName(), request);
    }

    @PostMapping("/api/profile/change-password")
    public String changePassword(Principal principal,
                                 @RequestBody ChangePasswordRequest request) {
        return profileService.changePassword(principal.getName(), request);
    }
}