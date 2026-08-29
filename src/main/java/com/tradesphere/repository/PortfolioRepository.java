package com.tradesphere.repository;

import com.tradesphere.model.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends MongoRepository<Portfolio, String> {

    Optional<Portfolio> findByUserIdAndSymbol(String userId, String symbol);

    List<Portfolio> findByUserId(String userId);
}