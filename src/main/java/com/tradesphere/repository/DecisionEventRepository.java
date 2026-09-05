package com.tradesphere.repository;

import com.tradesphere.model.DecisionEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DecisionEventRepository extends MongoRepository<DecisionEvent, String> {

    List<DecisionEvent> findByUserIdOrderByCreatedAtDesc(String userId);

    List<DecisionEvent> findByUserIdAndSymbolOrderByCreatedAtDesc(
            String userId,
            String symbol
    );
}