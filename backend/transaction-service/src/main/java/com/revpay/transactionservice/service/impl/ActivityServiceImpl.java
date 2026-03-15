package com.revpay.transactionservice.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.revpay.transactionservice.dto.response.ActivityResponse;
import com.revpay.transactionservice.entity.MoneyRequest;
import com.revpay.transactionservice.entity.Transaction;
import com.revpay.transactionservice.repository.MoneyRequestRepository;
import com.revpay.transactionservice.repository.TransactionRepository;
import com.revpay.transactionservice.service.ActivityService;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final TransactionRepository transactionRepository;
    private final MoneyRequestRepository moneyRequestRepository;

    public ActivityServiceImpl(TransactionRepository transactionRepository,
                               MoneyRequestRepository moneyRequestRepository) {
        this.transactionRepository = transactionRepository;
        this.moneyRequestRepository = moneyRequestRepository;
    }

    @Override
    public List<ActivityResponse> getMyActivity(Long userId) {

        List<ActivityResponse> activityList = new ArrayList<>();

        List<Transaction> transactions =
                transactionRepository.findBySenderUserIdOrReceiverUserIdOrderByCreatedAtDescIdDesc(userId, userId);

        for (Transaction transaction : transactions) {
            ActivityResponse response = new ActivityResponse();
            response.setActivityType(transaction.getType());
            response.setReference(transaction.getTransactionRef());
            response.setSenderUserId(transaction.getSenderUserId());
            response.setReceiverUserId(transaction.getReceiverUserId());
            response.setAmount(transaction.getAmount());
            response.setStatus(transaction.getStatus());
            response.setDescription(transaction.getDescription());
            response.setCreatedAt(transaction.getCreatedAt());
            activityList.add(response);
        }

        List<MoneyRequest> outgoingRequests =
                moneyRequestRepository.findByRequesterUserIdOrderByCreatedAtDescIdDesc(userId);

        for (MoneyRequest request : outgoingRequests) {
            ActivityResponse response = new ActivityResponse();
            response.setActivityType("REQUEST_SENT");
            response.setReference(request.getRequestRef());
            response.setRequesterUserId(request.getRequesterUserId());
            response.setPayerUserId(request.getPayerUserId());
            response.setAmount(request.getAmount());
            response.setStatus(request.getStatus());
            response.setDescription(request.getNote());
            response.setCreatedAt(request.getCreatedAt());
            activityList.add(response);
        }

        List<MoneyRequest> incomingRequests =
                moneyRequestRepository.findByPayerUserIdOrderByCreatedAtDescIdDesc(userId);

        for (MoneyRequest request : incomingRequests) {
            ActivityResponse response = new ActivityResponse();
            response.setActivityType("REQUEST_RECEIVED");
            response.setReference(request.getRequestRef());
            response.setRequesterUserId(request.getRequesterUserId());
            response.setPayerUserId(request.getPayerUserId());
            response.setAmount(request.getAmount());
            response.setStatus(request.getStatus());
            response.setDescription(request.getNote());
            response.setCreatedAt(request.getCreatedAt());
            activityList.add(response);
        }

        activityList.sort(Comparator.comparing(ActivityResponse::getCreatedAt).reversed());

        return activityList;
    }
}