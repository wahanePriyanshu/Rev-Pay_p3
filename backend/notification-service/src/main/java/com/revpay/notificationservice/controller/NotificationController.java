package com.revpay.notificationservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.revpay.notificationservice.dto.request.CreateNotificationRequest;
import com.revpay.notificationservice.dto.response.NotificationResponse;
import com.revpay.notificationservice.service.NotificationService;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/api/notifications")
    public List<NotificationResponse> getMyNotifications(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return notificationService.getMyNotifications(userId);
    }

    @PostMapping("/api/notifications/{id}/read")
    public NotificationResponse markAsRead(Authentication authentication,
                                           @PathVariable Long id) {
        Long userId = extractUserId(authentication);
        return notificationService.markAsRead(userId, id);
    }

    @PostMapping("/api/notifications/internal/create")
    public NotificationResponse createInternalNotification(@RequestBody CreateNotificationRequest request) {
        return notificationService.createInternalNotification(request);
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