package com.revpay.transactionservice.service.impl;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.revpay.transactionservice.client.WalletClient;
import com.revpay.transactionservice.dto.request.CreateMoneyRequestDto;
import com.revpay.transactionservice.dto.request.SendMoneyRequest;
import com.revpay.transactionservice.dto.response.MoneyRequestResponse;
import com.revpay.transactionservice.dto.response.TransactionResponse;
import com.revpay.transactionservice.entity.MoneyRequest;
import com.revpay.transactionservice.entity.Transaction;
import com.revpay.transactionservice.repository.MoneyRequestRepository;
import com.revpay.transactionservice.repository.TransactionRepository;
import com.revpay.transactionservice.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final MoneyRequestRepository moneyRequestRepository;
    private final WalletClient walletClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  MoneyRequestRepository moneyRequestRepository,
                                  WalletClient walletClient) {
        this.transactionRepository = transactionRepository;
        this.moneyRequestRepository = moneyRequestRepository;
        this.walletClient = walletClient;
    }

    @Override
    public TransactionResponse sendMoney(Long senderUserId, SendMoneyRequest request) {

        if (request.getReceiverUserId() == null) {
            throw new RuntimeException("Receiver user id is required");
        }

        if (senderUserId.equals(request.getReceiverUserId())) {
            throw new RuntimeException("Sender and receiver cannot be the same");
        }

        validateAmount(request.getAmount());

        WalletClient.WalletOperationRequest debitRequest = new WalletClient.WalletOperationRequest();
        debitRequest.setUserId(senderUserId);
        debitRequest.setAmount(request.getAmount());

        WalletClient.WalletOperationRequest creditRequest = new WalletClient.WalletOperationRequest();
        creditRequest.setUserId(request.getReceiverUserId());
        creditRequest.setAmount(request.getAmount());

        walletClient.debit(debitRequest);
        walletClient.credit(creditRequest);

        Transaction transaction = new Transaction();
        transaction.setTransactionRef(generateTransactionRef());
        transaction.setSenderUserId(senderUserId);
        transaction.setReceiverUserId(request.getReceiverUserId());
        transaction.setAmount(request.getAmount());
        transaction.setStatus("SUCCESS");
        transaction.setType("TRANSFER");
        transaction.setDescription(request.getDescription());

        Transaction saved = transactionRepository.save(transaction);

        return mapToTransactionResponse(saved);
    }

    @Override
    public MoneyRequestResponse createRequest(Long requesterUserId, CreateMoneyRequestDto request) {

        if (request.getPayerUserId() == null) {
            throw new RuntimeException("Payer user id is required");
        }

        if (requesterUserId.equals(request.getPayerUserId())) {
            throw new RuntimeException("Requester and payer cannot be the same");
        }

        validateAmount(request.getAmount());

        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setRequestRef(generateRequestRef());
        moneyRequest.setRequesterUserId(requesterUserId);
        moneyRequest.setPayerUserId(request.getPayerUserId());
        moneyRequest.setAmount(request.getAmount());
        moneyRequest.setNote(request.getNote());
        moneyRequest.setStatus("PENDING");

        MoneyRequest saved = moneyRequestRepository.save(moneyRequest);

        return mapToMoneyRequestResponse(saved);
    }

    @Override
    public MoneyRequestResponse acceptRequest(Long currentUserId, Long requestId) {

        MoneyRequest moneyRequest = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Money request not found"));

        if (!currentUserId.equals(moneyRequest.getPayerUserId())) {
            throw new RuntimeException("You are not allowed to accept this request");
        }

        if (!"PENDING".equals(moneyRequest.getStatus())) {
            throw new RuntimeException("Only pending requests can be accepted");
        }

        WalletClient.WalletOperationRequest debitRequest = new WalletClient.WalletOperationRequest();
        debitRequest.setUserId(moneyRequest.getPayerUserId());
        debitRequest.setAmount(moneyRequest.getAmount());

        WalletClient.WalletOperationRequest creditRequest = new WalletClient.WalletOperationRequest();
        creditRequest.setUserId(moneyRequest.getRequesterUserId());
        creditRequest.setAmount(moneyRequest.getAmount());

        walletClient.debit(debitRequest);
        walletClient.credit(creditRequest);

        moneyRequest.setStatus("ACCEPTED");
        MoneyRequest savedRequest = moneyRequestRepository.save(moneyRequest);

        Transaction transaction = new Transaction();
        transaction.setTransactionRef(generateTransactionRef());
        transaction.setSenderUserId(moneyRequest.getPayerUserId());
        transaction.setReceiverUserId(moneyRequest.getRequesterUserId());
        transaction.setAmount(moneyRequest.getAmount());
        transaction.setStatus("SUCCESS");
        transaction.setType("REQUEST_ACCEPTED");
        transaction.setDescription(moneyRequest.getNote());

        transactionRepository.save(transaction);

        return mapToMoneyRequestResponse(savedRequest);
    }

    @Override
    public MoneyRequestResponse declineRequest(Long currentUserId, Long requestId) {

        MoneyRequest moneyRequest = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Money request not found"));

        if (!currentUserId.equals(moneyRequest.getPayerUserId())) {
            throw new RuntimeException("You are not allowed to decline this request");
        }

        if (!"PENDING".equals(moneyRequest.getStatus())) {
            throw new RuntimeException("Only pending requests can be declined");
        }

        moneyRequest.setStatus("DECLINED");
        MoneyRequest saved = moneyRequestRepository.save(moneyRequest);

        return mapToMoneyRequestResponse(saved);
    }

    @Override
    public List<MoneyRequestResponse> getOutgoingRequests(Long requesterUserId) {
        return moneyRequestRepository.findByRequesterUserIdOrderByCreatedAtDesc(requesterUserId)
                .stream()
                .map(this::mapToMoneyRequestResponse)
                .toList();
    }

    @Override
    public List<MoneyRequestResponse> getIncomingRequests(Long payerUserId) {
        return moneyRequestRepository.findByPayerUserIdOrderByCreatedAtDesc(payerUserId)
                .stream()
                .map(this::mapToMoneyRequestResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> getMyTransactions(Long userId) {
        return transactionRepository.findBySenderUserIdOrReceiverUserIdOrderByCreatedAtDesc(userId, userId)
                .stream()
                .map(this::mapToTransactionResponse)
                .toList();
    }

    private void validateAmount(java.math.BigDecimal amount) {
        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
    }

    private String generateTransactionRef() {
        return "TXN" + (100000 + new Random().nextInt(900000));
    }

    private String generateRequestRef() {
        return "REQ" + (100000 + new Random().nextInt(900000));
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setTransactionRef(transaction.getTransactionRef());
        response.setSenderUserId(transaction.getSenderUserId());
        response.setReceiverUserId(transaction.getReceiverUserId());
        response.setAmount(transaction.getAmount());
        response.setStatus(transaction.getStatus());
        response.setType(transaction.getType());
        response.setDescription(transaction.getDescription());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }

    private MoneyRequestResponse mapToMoneyRequestResponse(MoneyRequest moneyRequest) {
        MoneyRequestResponse response = new MoneyRequestResponse();
        response.setId(moneyRequest.getId());
        response.setRequestRef(moneyRequest.getRequestRef());
        response.setRequesterUserId(moneyRequest.getRequesterUserId());
        response.setPayerUserId(moneyRequest.getPayerUserId());
        response.setAmount(moneyRequest.getAmount());
        response.setNote(moneyRequest.getNote());
        response.setStatus(moneyRequest.getStatus());
        response.setCreatedAt(moneyRequest.getCreatedAt());
        return response;
    }
}