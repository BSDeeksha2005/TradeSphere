package com.tradesphere.service;

import com.tradesphere.dto.BuyRequest;
import com.tradesphere.dto.BuyResponse;
import com.tradesphere.dto.SellRequest;
import com.tradesphere.dto.SellResponse;
import com.tradesphere.model.Asset;
import com.tradesphere.model.Portfolio;
import com.tradesphere.model.User;
import com.tradesphere.repository.AssetRepository;
import com.tradesphere.repository.PortfolioRepository;
import com.tradesphere.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final TransactionService transactionService;

    public PortfolioService(
            PortfolioRepository portfolioRepository,
            UserRepository userRepository,
            AssetRepository assetRepository,
            TransactionService transactionService) {

        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.transactionService = transactionService;
    }

    // ------------------------------------------------------------
    // BUY
    // ------------------------------------------------------------

    public BuyResponse buyStock(String email, BuyRequest buyRequest) {

        validateQuantity(buyRequest.getQuantity());

        User user = getUser(email);

        Asset asset = getAsset(buyRequest.getSymbol());

        int quantity = buyRequest.getQuantity();
        double pricePerShare = asset.getPrice();
        double totalCost = pricePerShare * quantity;

        if (user.getBalance() < totalCost) {
            throw new IllegalStateException(
                    "Insufficient balance to complete this purchase");
        }

        Optional<Portfolio> existingHolding =
                portfolioRepository.findByUserIdAndSymbol(
                        user.getId(),
                        asset.getSymbol());

        Portfolio portfolio;

        if (existingHolding.isPresent()) {

            portfolio = existingHolding.get();

            int oldQuantity = portfolio.getQuantity();
            double oldAveragePrice = portfolio.getAveragePrice();

            int newQuantity = oldQuantity + quantity;

            double newAveragePrice =
                    ((oldQuantity * oldAveragePrice)
                            + (quantity * pricePerShare))
                            / newQuantity;

            portfolio.setQuantity(newQuantity);
            portfolio.setAveragePrice(newAveragePrice);

        } else {

            portfolio = new Portfolio(
                    user.getId(),
                    asset.getSymbol(),
                    quantity,
                    pricePerShare
            );
        }

        /*
         * Perform all validations before modifying persistent state.
         * The writes below represent one logical trading operation.
         */
        user.setBalance(user.getBalance() - totalCost);
        userRepository.save(user);

        portfolioRepository.save(portfolio);

        transactionService.recordTransaction(
                user.getId(),
                asset.getSymbol(),
                "BUY",
                quantity,
                pricePerShare,
                totalCost
        );

        return new BuyResponse(
                asset.getSymbol(),
                quantity,
                pricePerShare,
                totalCost,
                user.getBalance()
        );
    }

    // ------------------------------------------------------------
    // SELL
    // ------------------------------------------------------------

    public SellResponse sellStock(
            String email,
            SellRequest sellRequest) {

        validateQuantity(sellRequest.getQuantity());

        User user = getUser(email);

        Asset asset = getAsset(sellRequest.getSymbol());

        Portfolio portfolio =
                portfolioRepository.findByUserIdAndSymbol(
                        user.getId(),
                        asset.getSymbol())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "You do not own any shares of "
                                        + asset.getSymbol()));

        int quantity = sellRequest.getQuantity();

        if (quantity > portfolio.getQuantity()) {
            throw new IllegalStateException(
                    "Cannot sell more shares than you own");
        }

        double pricePerShare = asset.getPrice();
        double totalValue = pricePerShare * quantity;

        int remainingQuantity =
                portfolio.getQuantity() - quantity;

        /*
         * All validation is complete before persistent state changes.
         */
        user.setBalance(user.getBalance() + totalValue);
        userRepository.save(user);

        if (remainingQuantity == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolio.setQuantity(remainingQuantity);
            portfolioRepository.save(portfolio);
        }

        transactionService.recordTransaction(
                user.getId(),
                asset.getSymbol(),
                "SELL",
                quantity,
                pricePerShare,
                totalValue
        );

        return new SellResponse(
                asset.getSymbol(),
                quantity,
                pricePerShare,
                totalValue,
                user.getBalance()
        );
    }

    // ------------------------------------------------------------
    // VIEW PORTFOLIO
    // ------------------------------------------------------------

    public List<Portfolio> getUserPortfolio(String email) {

        User user = getUser(email);

        return portfolioRepository.findByUserId(user.getId());
    }

    // ------------------------------------------------------------
    // VALIDATION / LOOKUPS
    // ------------------------------------------------------------

    private void validateQuantity(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0");
        }
    }

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));
    }

    private Asset getAsset(String symbol) {

        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException(
                    "Symbol is required");
        }

        return assetRepository.findBySymbol(
                        symbol.toUpperCase())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Asset not found for symbol: "
                                        + symbol));
    }
}
