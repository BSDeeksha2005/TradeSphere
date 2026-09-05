package com.tradesphere.config;

import com.tradesphere.model.Asset;
import com.tradesphere.repository.AssetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAssets(AssetRepository assetRepository) {
        return args -> {

            List<Asset> assets = List.of(
                    new Asset("AAPL", "Apple Inc.", "Technology", 227.50),
                    new Asset("MSFT", "Microsoft Corporation", "Technology", 506.70),
                    new Asset("NVDA", "NVIDIA Corporation", "Technology", 174.00),
                    new Asset("AMZN", "Amazon.com Inc.", "Consumer", 231.50),
                    new Asset("GOOGL", "Alphabet Inc.", "Technology", 202.00),
                    new Asset("META", "Meta Platforms Inc.", "Technology", 770.00),
                    new Asset("TSLA", "Tesla Inc.", "Automotive", 350.00),
                    new Asset("AMD", "Advanced Micro Devices", "Technology", 165.00),
                    new Asset("JPM", "JPMorgan Chase & Co.", "Financials", 300.00),
                    new Asset("V", "Visa Inc.", "Financials", 340.00),
                    new Asset("MA", "Mastercard Inc.", "Financials", 580.00),
                    new Asset("BAC", "Bank of America Corp.", "Financials", 50.00),
                    new Asset("JNJ", "Johnson & Johnson", "Healthcare", 175.00),
                    new Asset("PFE", "Pfizer Inc.", "Healthcare", 25.00),
                    new Asset("UNH", "UnitedHealth Group", "Healthcare", 280.00),
                    new Asset("WMT", "Walmart Inc.", "Consumer", 100.00),
                    new Asset("KO", "The Coca-Cola Company", "Consumer", 70.00),
                    new Asset("MCD", "McDonald's Corporation", "Consumer", 310.00),
                    new Asset("NFLX", "Netflix Inc.", "Entertainment", 1200.00),
                    new Asset("DIS", "The Walt Disney Company", "Entertainment", 115.00)
            );

            for (Asset asset : assets) {
                if (assetRepository.findBySymbol(asset.getSymbol()).isEmpty()) {
                    assetRepository.save(asset);
                }
            }

            System.out.println("========================================");
            System.out.println("TradeSphere: Asset seed completed!");
            System.out.println("Assets available: " + assetRepository.count());
            System.out.println("========================================");
        };
    }
}