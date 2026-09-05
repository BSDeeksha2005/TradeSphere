package com.tradesphere.repository;

import com.tradesphere.model.Conviction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConvictionRepository
        extends MongoRepository<Conviction, String> {

    List<Conviction> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Conviction> findByUserIdAndSymbolOrderByCreatedAtDesc(
            String userId,
            String symbol
    );
}