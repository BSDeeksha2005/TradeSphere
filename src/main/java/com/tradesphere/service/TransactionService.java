package com.tradesphere.service;

import com.tradesphere.model.Transaction;
import com.tradesphere.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction recordTransaction(
            String userId,
            String symbol,
            String type,
            int quantity,
            double price,
            double totalValue) {

        Transaction transaction = new Transaction(
                userId,
                symbol,
                type,
                quantity,
                price,
                totalValue,
                new Date()
        );

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getUserTransactions(String userId) {
        return transactionRepository
                .findByUserIdOrderByTimestampDesc(userId);
    }
}