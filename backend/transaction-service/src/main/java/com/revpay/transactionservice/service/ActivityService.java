package com.revpay.transactionservice.service;

import java.util.List;

import com.revpay.transactionservice.dto.response.ActivityResponse;

public interface ActivityService {
    List<ActivityResponse> getMyActivity(Long userId);
}