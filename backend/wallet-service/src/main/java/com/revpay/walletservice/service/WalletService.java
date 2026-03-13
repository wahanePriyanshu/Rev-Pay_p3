package com.revpay.walletservice.service;

import com.revpay.walletservice.dto.request.AddMoneyRequest;
import com.revpay.walletservice.dto.request.WalletOperationRequest;
import com.revpay.walletservice.dto.request.WithdrawMoneyRequest;
import com.revpay.walletservice.dto.response.WalletBalanceResponse;
import com.revpay.walletservice.dto.response.WalletResponse;

public interface WalletService {
    WalletResponse getMyWallet(Long userId);
    WalletBalanceResponse getMyBalance(Long userId);
    WalletResponse addMoney(Long userId, AddMoneyRequest request);
    WalletResponse withdrawMoney(Long userId, WithdrawMoneyRequest request);
    
    
    void debitInternal(WalletOperationRequest request);
    void creditInternal(WalletOperationRequest request);
}
