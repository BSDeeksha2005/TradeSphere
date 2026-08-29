package com.tradesphere.repository;

import com.tradesphere.model.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AssetRepository extends MongoRepository<Asset, String> {

    Optional<Asset> findBySymbol(String symbol);
}