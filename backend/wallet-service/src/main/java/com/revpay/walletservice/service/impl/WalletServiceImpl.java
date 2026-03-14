package com.revpay.walletservice.service.impl;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.revpay.walletservice.dto.request.AddMoneyRequest;
import com.revpay.walletservice.dto.request.WalletOperationRequest;
import com.revpay.walletservice.dto.request.WithdrawMoneyRequest;
import com.revpay.walletservice.dto.response.WalletBalanceResponse;
import com.revpay.walletservice.dto.response.WalletResponse;
import com.revpay.walletservice.entity.Wallet;
import com.revpay.walletservice.repository.WalletRepository;
import com.revpay.walletservice.service.WalletService;
import com.revpay.walletservice.client.NotificationClient.*;
import com.revpay.walletservice.client.TransactionClient.TransactionClient;

@Service
public class WalletServiceImpl implements WalletService {

	private final WalletRepository walletRepository;

	private final NotificationClient notificationClient;
	private final TransactionClient transactionClient;

	public WalletServiceImpl(WalletRepository walletRepository, NotificationClient notificationClient, TransactionClient transactionClient) {
		this.walletRepository = walletRepository;
		this.notificationClient = notificationClient;
		this.transactionClient = transactionClient;
	}

	@Override
	public WalletResponse getMyWallet(Long userId) {
		Wallet wallet = getOrCreateWallet(userId);
		return mapToWalletResponse(wallet);
	}

	@Override
	public WalletBalanceResponse getMyBalance(Long userId) {
		Wallet wallet = getOrCreateWallet(userId);

		WalletBalanceResponse response = new WalletBalanceResponse();
		response.setWalletId(wallet.getId());
		response.setBalance(wallet.getBalance());
		response.setHasPin(true); // temporary
		return response;
	}

	@Override
	public WalletResponse addMoney(Long userId, AddMoneyRequest request) {
		Wallet wallet = getOrCreateWallet(userId);

		validateAmount(request.getAmount());

		wallet.setBalance(wallet.getBalance().add(request.getAmount()));
		Wallet saved = walletRepository.save(wallet);

		createNotification(userId, "Money added", "₹" + request.getAmount() + " was added to your wallet",
				"TRANSACTION", "WALLET", saved.getId());

		TransactionClient.CreateTransactionRequest txRequest = new TransactionClient.CreateTransactionRequest();
		txRequest.setUserId(userId);
		txRequest.setAmount(request.getAmount());
		txRequest.setType("ADD_MONEY");
		txRequest.setDescription("Added money to wallet");
		transactionClient.createTransaction(txRequest);

		return mapToWalletResponse(saved);
	}

	@Override
	public WalletResponse withdrawMoney(Long userId, WithdrawMoneyRequest request) {
		Wallet wallet = getOrCreateWallet(userId);

		validateAmount(request.getAmount());

		if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
			throw new RuntimeException("Insufficient balance");
		}

		wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
		Wallet saved = walletRepository.save(wallet);

		createNotification(userId, "Money withdrawn", "₹" + request.getAmount() + " was withdrawn from your wallet",
				"TRANSACTION", "WALLET", saved.getId());

		if (saved.getBalance().compareTo(new BigDecimal("500")) < 0) {
			createNotification(userId, "Low balance alert", "Your wallet balance is low: ₹" + saved.getBalance(),
					"LOW_BALANCE", "WALLET", saved.getId());
		}

		TransactionClient.CreateTransactionRequest txRequest = new TransactionClient.CreateTransactionRequest();
		txRequest.setUserId(userId);
		txRequest.setAmount(request.getAmount());
		txRequest.setType("WITHDRAW");
		txRequest.setDescription("Withdrawn to bank");
		transactionClient.createTransaction(txRequest);

		return mapToWalletResponse(saved);
	}

	@Override
	public void debitInternal(WalletOperationRequest request) {
		validateInternalRequest(request);

		Wallet wallet = getOrCreateWallet(request.getUserId());

		if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
			throw new RuntimeException("Insufficient balance");
		}

		wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
		walletRepository.save(wallet);
	}

	@Override
	public void creditInternal(WalletOperationRequest request) {
		validateInternalRequest(request);

		Wallet wallet = getOrCreateWallet(request.getUserId());

		wallet.setBalance(wallet.getBalance().add(request.getAmount()));
		walletRepository.save(wallet);
	}

	private Wallet getOrCreateWallet(Long userId) {
		return walletRepository.findByUserId(userId).orElseGet(() -> {
			Wallet wallet = new Wallet();
			wallet.setUserId(userId);
			wallet.setWalletNumber(generateWalletNumber());
			wallet.setBalance(BigDecimal.ZERO);
			return walletRepository.save(wallet);
		});
	}

	private WalletResponse mapToWalletResponse(Wallet wallet) {
		WalletResponse response = new WalletResponse();
		response.setId(wallet.getId());
		response.setUserId(wallet.getUserId());
		response.setWalletNumber(wallet.getWalletNumber());
		response.setBalance(wallet.getBalance());
		response.setCurrency(wallet.getCurrency());
		response.setStatus(wallet.getStatus());
		return response;
	}

	private void validateAmount(BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("Amount must be greater than zero");
		}
	}

	private void validateInternalRequest(WalletOperationRequest request) {
		if (request == null) {
			throw new RuntimeException("Request cannot be null");
		}
		if (request.getUserId() == null) {
			throw new RuntimeException("User id is required");
		}
		validateAmount(request.getAmount());
	}

	private String generateWalletNumber() {
		return "WALLET" + (100000 + new Random().nextInt(900000));
	}

	private void createNotification(Long userId, String title, String message, String type, String referenceType,
			Long referenceId) {
		NotificationClient.CreateNotificationRequest request = new NotificationClient.CreateNotificationRequest();
		request.setUserId(userId);
		request.setTitle(title);
		request.setMessage(message);
		request.setType(type);
		request.setReferenceType(referenceType);
		request.setReferenceId(referenceId);

		notificationClient.createNotification(request);
	}

}