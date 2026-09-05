package com.tradesphere.service;

import com.tradesphere.model.Asset;
import com.tradesphere.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public Asset createAsset(Asset asset) {

        if (asset.getSymbol() == null || asset.getSymbol().isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be blank");
        }

        if (asset.getName() == null || asset.getName().isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }

        if (asset.getSector() == null || asset.getSector().isBlank()) {
            throw new IllegalArgumentException("Sector cannot be blank");
        }

        if (asset.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        asset.setSymbol(asset.getSymbol().trim().toUpperCase());
        asset.setName(asset.getName().trim());
        asset.setSector(asset.getSector().trim());

        Optional<Asset> existingAsset =
                assetRepository.findBySymbol(asset.getSymbol());

        if (existingAsset.isPresent()) {
            throw new IllegalStateException(
                    "Asset with this symbol already exists"
            );
        }

        return assetRepository.save(asset);
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Asset getAssetBySymbol(String symbol) {
        return assetRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Asset not found for symbol: " + symbol
                ));
    }
}