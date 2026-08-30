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

        if (buyRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Asset asset = assetRepository.findBySymbol(
                        buyRequest.getSymbol())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Asset not found for symbol: "
                                        + buyRequest.getSymbol()));

        double pricePerShare = asset.getPrice();
        double totalCost =
                pricePerShare * buyRequest.getQuantity();

        if (user.getBalance() < totalCost) {
            throw new IllegalStateException(
                    "Insufficient balance to complete this purchase");
        }

        user.setBalance(user.getBalance() - totalCost);
        userRepository.save(user);

        Optional<Portfolio> existingHolding =
                portfolioRepository.findByUserIdAndSymbol(
                        user.getId(),
                        asset.getSymbol());

        Portfolio portfolio;

        if (existingHolding.isPresent()) {

            portfolio = existingHolding.get();

            int oldQuantity = portfolio.getQuantity();
            double oldAveragePrice = portfolio.getAveragePrice();

            int newQuantity =
                    oldQuantity + buyRequest.getQuantity();

            double newAveragePrice =
                    ((oldQuantity * oldAveragePrice)
                            + (buyRequest.getQuantity()
                            * pricePerShare))
                            / newQuantity;

            portfolio.setQuantity(newQuantity);
            portfolio.setAveragePrice(newAveragePrice);

        } else {

            portfolio = new Portfolio(
                    user.getId(),
                    asset.getSymbol(),
                    buyRequest.getQuantity(),
                    pricePerShare
            );
        }

        portfolioRepository.save(portfolio);

        transactionService.recordTransaction(
                user.getId(),
                asset.getSymbol(),
                "BUY",
                buyRequest.getQuantity(),
                pricePerShare,
                totalCost
        );

        return new BuyResponse(
                asset.getSymbol(),
                buyRequest.getQuantity(),
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

        if (sellRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Asset asset = assetRepository.findBySymbol(
                        sellRequest.getSymbol())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Asset not found for symbol: "
                                        + sellRequest.getSymbol()));

        Portfolio portfolio =
                portfolioRepository.findByUserIdAndSymbol(
                        user.getId(),
                        asset.getSymbol())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "You do not own any shares of "
                                        + asset.getSymbol()));

        if (sellRequest.getQuantity()
                > portfolio.getQuantity()) {

            throw new IllegalStateException(
                    "Cannot sell more shares than you own");
        }

        double pricePerShare = asset.getPrice();

        double totalValue =
                pricePerShare * sellRequest.getQuantity();

        user.setBalance(user.getBalance() + totalValue);
        userRepository.save(user);

        int remainingQuantity =
                portfolio.getQuantity()
                        - sellRequest.getQuantity();

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
                sellRequest.getQuantity(),
                pricePerShare,
                totalValue
        );

        return new SellResponse(
                asset.getSymbol(),
                sellRequest.getQuantity(),
                pricePerShare,
                totalValue,
                user.getBalance()
        );
    }

    // ------------------------------------------------------------
    // VIEW PORTFOLIO
    // ------------------------------------------------------------

    public List<Portfolio> getUserPortfolio(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return portfolioRepository.findByUserId(user.getId());
    }
}