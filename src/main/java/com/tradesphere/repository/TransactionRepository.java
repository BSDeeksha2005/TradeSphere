package com.tradesphere.repository;

import com.tradesphere.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository
        extends MongoRepository<Transaction, String> {

    List<Transaction> findByUserIdOrderByTimestampDesc(String userId);
}