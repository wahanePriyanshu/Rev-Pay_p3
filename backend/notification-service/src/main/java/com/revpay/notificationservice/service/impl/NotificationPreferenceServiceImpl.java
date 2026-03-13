package com.revpay.notificationservice.service.impl;

import org.springframework.stereotype.Service;

import com.revpay.notificationservice.dto.request.UpdateNotificationPreferenceRequest;
import com.revpay.notificationservice.dto.response.NotificationPreferenceResponse;
import com.revpay.notificationservice.entity.NotificationPreference;
import com.revpay.notificationservice.repository.NotificationPreferenceRepository;
import com.revpay.notificationservice.service.NotificationPreferenceService;

@Service
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationPreferenceServiceImpl(NotificationPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public NotificationPreferenceResponse getMyPreferences(Long userId) {
        NotificationPreference preference = getOrCreatePreference(userId);
        return mapToResponse(preference);
    }

    @Override
    public NotificationPreferenceResponse updateMyPreferences(Long userId, UpdateNotificationPreferenceRequest request) {
        NotificationPreference preference = getOrCreatePreference(userId);

        if (request.getTransactionAlerts() != null) preference.setTransactionAlerts(request.getTransactionAlerts());
        if (request.getRequestAlerts() != null) preference.setRequestAlerts(request.getRequestAlerts());
        if (request.getInvoiceAlerts() != null) preference.setInvoiceAlerts(request.getInvoiceAlerts());
        if (request.getLoanAlerts() != null) preference.setLoanAlerts(request.getLoanAlerts());
        if (request.getLowBalanceAlerts() != null) preference.setLowBalanceAlerts(request.getLowBalanceAlerts());
        if (request.getSystemAlerts() != null) preference.setSystemAlerts(request.getSystemAlerts());

        NotificationPreference saved = preferenceRepository.save(preference);
        return mapToResponse(saved);
    }

    private NotificationPreference getOrCreatePreference(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreference preference = new NotificationPreference();
                    preference.setUserId(userId);
                    return preferenceRepository.save(preference);
                });
    }

    private NotificationPreferenceResponse mapToResponse(NotificationPreference preference) {
        NotificationPreferenceResponse response = new NotificationPreferenceResponse();
        response.setId(preference.getId());
        response.setUserId(preference.getUserId());
        response.setTransactionAlerts(preference.getTransactionAlerts());
        response.setRequestAlerts(preference.getRequestAlerts());
        response.setInvoiceAlerts(preference.getInvoiceAlerts());
        response.setLoanAlerts(preference.getLoanAlerts());
        response.setLowBalanceAlerts(preference.getLowBalanceAlerts());
        response.setSystemAlerts(preference.getSystemAlerts());
        return response;
    }
}