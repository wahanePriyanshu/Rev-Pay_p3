package com.revpay.transactionservice.service.export.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.revpay.transactionservice.entity.Transaction;
import com.revpay.transactionservice.repository.TransactionRepository;
import com.revpay.transactionservice.service.export.TransactionExportService;

@Service
public class TransactionExportServiceImpl implements TransactionExportService {

    private final TransactionRepository transactionRepository;

    public TransactionExportServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public ByteArrayInputStream exportTransactionsToCSV(Long userId) {

        List<Transaction> transactions =
                transactionRepository.findBySenderUserIdOrReceiverUserIdOrderByCreatedAtDesc(userId, userId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (
            PrintWriter writer = new PrintWriter(out);
            CSVPrinter csvPrinter = new CSVPrinter(writer,
                    CSVFormat.DEFAULT.withHeader(
                            "TransactionRef",
                            "Sender",
                            "Receiver",
                            "Amount",
                            "Status",
                            "Type",
                            "CreatedAt"))
        ) {

            for (Transaction tx : transactions) {

                csvPrinter.printRecord(
                        tx.getTransactionRef(),
                        tx.getSenderUserId(),
                        tx.getReceiverUserId(),
                        tx.getAmount(),
                        tx.getStatus(),
                        tx.getType(),
                        tx.getCreatedAt()
                );

            }

            csvPrinter.flush();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV");
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportTransactionsToPDF(Long userId) {

        List<Transaction> transactions =
                transactionRepository.findBySenderUserIdOrReceiverUserIdOrderByCreatedAtDesc(userId, userId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            Document document = new Document();

            PdfWriter.getInstance(document, out);

            document.open();

            document.add(new Paragraph("RevPay Transaction Report"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);

            table.addCell("TransactionRef");
            table.addCell("Sender");
            table.addCell("Receiver");
            table.addCell("Amount");
            table.addCell("Status");
            table.addCell("Type");

            for (Transaction tx : transactions) {

                table.addCell(tx.getTransactionRef());
                table.addCell(String.valueOf(tx.getSenderUserId()));
                table.addCell(String.valueOf(tx.getReceiverUserId()));
                table.addCell(tx.getAmount().toString());
                table.addCell(tx.getStatus());
                table.addCell(tx.getType());

            }

            document.add(table);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export PDF");
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}