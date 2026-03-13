package com.revpay.transactionservice.service.export;

import java.io.ByteArrayInputStream;

public interface TransactionExportService {

    ByteArrayInputStream exportTransactionsToCSV(Long userId);

    ByteArrayInputStream exportTransactionsToPDF(Long userId);

}
