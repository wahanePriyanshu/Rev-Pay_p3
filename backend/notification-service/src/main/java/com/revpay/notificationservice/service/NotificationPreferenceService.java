package com.revpay.notificationservice.service;

import com.revpay.notificationservice.dto.request.UpdateNotificationPreferenceRequest;
import com.revpay.notificationservice.dto.response.NotificationPreferenceResponse;

public interface NotificationPreferenceService {
    NotificationPreferenceResponse getMyPreferences(Long userId);
    NotificationPreferenceResponse updateMyPreferences(Long userId, UpdateNotificationPreferenceRequest request);
}