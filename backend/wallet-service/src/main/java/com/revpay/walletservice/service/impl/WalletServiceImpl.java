package com.revpay.walletservice.service.impl;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.revpay.walletservice.dto.request.AddMoneyRequest;
import com.revpay.walletservice.dto.request.WithdrawMoneyRequest;
import com.revpay.walletservice.dto.response.WalletBalanceResponse;
import com.revpay.walletservice.dto.response.WalletResponse;
import com.revpay.walletservice.entity.Wallet;
import com.revpay.walletservice.repository.WalletRepository;
import com.revpay.walletservice.service.WalletService;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
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

        return mapToWalletResponse(saved);
    }

    private Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
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

    private String generateWalletNumber() {
        return "WALLET" + (100000 + new Random().nextInt(900000));
    }
}