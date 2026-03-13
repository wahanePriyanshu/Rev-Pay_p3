package com.revpay.transactionservice.service;

import java.util.List;

import com.revpay.transactionservice.dto.request.CreateMoneyRequestDto;
import com.revpay.transactionservice.dto.request.SendMoneyRequest;
import com.revpay.transactionservice.dto.response.MoneyRequestResponse;
import com.revpay.transactionservice.dto.response.TransactionResponse;

public interface TransactionService {
    TransactionResponse sendMoney(Long senderUserId, SendMoneyRequest request);
    MoneyRequestResponse createRequest(Long requesterUserId, CreateMoneyRequestDto request);
    MoneyRequestResponse acceptRequest(Long currentUserId, Long requestId);
    MoneyRequestResponse declineRequest(Long currentUserId, Long requestId);
    List<MoneyRequestResponse> getOutgoingRequests(Long requesterUserId);
    List<MoneyRequestResponse> getIncomingRequests(Long payerUserId);
    List<TransactionResponse> getMyTransactions(Long userId);
}