package com.revpay.notificationservice.service;

import java.util.List;

import com.revpay.notificationservice.dto.request.CreateNotificationRequest;
import com.revpay.notificationservice.dto.response.NotificationResponse;

public interface NotificationService {
    List<NotificationResponse> getMyNotifications(Long userId);
    NotificationResponse markAsRead(Long userId, Long notificationId);
    NotificationResponse createInternalNotification(CreateNotificationRequest request);
}