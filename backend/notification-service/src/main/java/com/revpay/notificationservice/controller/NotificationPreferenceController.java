package com.revpay.notificationservice.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.revpay.notificationservice.dto.request.UpdateNotificationPreferenceRequest;
import com.revpay.notificationservice.dto.response.NotificationPreferenceResponse;
import com.revpay.notificationservice.service.NotificationPreferenceService;

@RestController
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    public NotificationPreferenceController(NotificationPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @GetMapping("/api/notification-preferences")
    public NotificationPreferenceResponse getMyPreferences(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return preferenceService.getMyPreferences(userId);
    }

    @PutMapping("/api/notification-preferences")
    public NotificationPreferenceResponse updateMyPreferences(Authentication authentication,
                                                              @RequestBody UpdateNotificationPreferenceRequest request) {
        Long userId = extractUserId(authentication);
        return preferenceService.updateMyPreferences(userId, request);
    }

    @SuppressWarnings("unchecked")
    private Long extractUserId(Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Object value = details.get("userId");

        if (value instanceof Integer i) return i.longValue();
        if (value instanceof Long l) return l;
        return Long.valueOf(value.toString());
    }
}