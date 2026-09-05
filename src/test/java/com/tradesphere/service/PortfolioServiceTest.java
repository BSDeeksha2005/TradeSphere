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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PortfolioServiceTest {

    private PortfolioRepository portfolioRepository;
    private UserRepository userRepository;
    private AssetRepository assetRepository;
    private TransactionService transactionService;

    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        portfolioRepository = mock(PortfolioRepository.class);
        userRepository = mock(UserRepository.class);
        assetRepository = mock(AssetRepository.class);
        transactionService = mock(TransactionService.class);

        portfolioService = new PortfolioService(
                portfolioRepository,
                userRepository,
                assetRepository,
                transactionService
        );
    }

    // ------------------------------------------------------------
    // BUY TESTS
    // ------------------------------------------------------------

    @Test
    void buyStockShouldRejectZeroQuantity() {

        BuyRequest request = new BuyRequest("MSFT", 0);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> portfolioService.buyStock(
                                "test@example.com",
                                request
                        )
                );

        assertEquals(
                "Quantity must be greater than 0",
                exception.getMessage()
        );
    }

    @Test
    void buyStockShouldRejectNegativeQuantity() {

        BuyRequest request = new BuyRequest("MSFT", -5);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> portfolioService.buyStock(
                                "test@example.com",
                                request
                        )
                );

        assertEquals(
                "Quantity must be greater than 0",
                exception.getMessage()
        );
    }

    @Test
    void buyStockShouldRejectWhenBalanceIsInsufficient() {

        User user = new User(
                "Test User",
                "test@example.com",
                "password",
                "USER",
                1000.0
        );

        user.setId("user-1");

        Asset asset = new Asset(
                "MSFT",
                "Microsoft",
                "Technology",
                500.0
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(assetRepository.findBySymbol("MSFT"))
                .thenReturn(Optional.of(asset));

        BuyRequest request = new BuyRequest("MSFT", 3);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> portfolioService.buyStock(
                                "test@example.com",
                                request
                        )
                );

        assertEquals(
                "Insufficient balance to complete this purchase",
                exception.getMessage()
        );
    }

    @Test
    void buyStockShouldSuccessfullyCreateNewHolding() {

        User user = new User(
                "Test User",
                "test@example.com",
                "password",
                "USER",
                10000.0
        );

        user.setId("user-1");

        Asset asset = new Asset(
                "MSFT",
                "Microsoft",
                "Technology",
                500.0
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(assetRepository.findBySymbol("MSFT"))
                .thenReturn(Optional.of(asset));

        when(portfolioRepository.findByUserIdAndSymbol(
                "user-1",
                "MSFT"
        )).thenReturn(Optional.empty());

        BuyRequest request = new BuyRequest("MSFT", 3);

        BuyResponse response =
                portfolioService.buyStock(
                        "test@example.com",
                        request
                );

        assertEquals("MSFT", response.getSymbol());
        assertEquals(3, response.getQuantity());
        assertEquals(500.0, response.getPricePerShare());
        assertEquals(1500.0, response.getTotalCost());
        assertEquals(8500.0, response.getRemainingBalance());

        assertEquals(8500.0, user.getBalance());

        verify(userRepository).save(user);
        verify(portfolioRepository).save(any(Portfolio.class));

        verify(transactionService).recordTransaction(
                "user-1",
                "MSFT",
                "BUY",
                3,
                500.0,
                1500.0
        );
    }

    // ------------------------------------------------------------
    // SELL TESTS
    // ------------------------------------------------------------

    @Test
    void sellStockShouldRejectNegativeQuantity() {

        SellRequest request = new SellRequest("MSFT", -2);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> portfolioService.sellStock(
                                "test@example.com",
                                request
                        )
                );

        assertEquals(
                "Quantity must be greater than 0",
                exception.getMessage()
        );
    }

    @Test
    void sellStockShouldRejectWhenSellingMoreThanOwned() {

        User user = new User(
                "Test User",
                "test@example.com",
                "password",
                "USER",
                1000.0
        );

        user.setId("user-1");

        Asset asset = new Asset(
                "MSFT",
                "Microsoft",
                "Technology",
                500.0
        );

        Portfolio portfolio = mock(Portfolio.class);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(assetRepository.findBySymbol("MSFT"))
                .thenReturn(Optional.of(asset));

        when(portfolioRepository.findByUserIdAndSymbol(
                "user-1",
                "MSFT"
        )).thenReturn(Optional.of(portfolio));

        when(portfolio.getQuantity()).thenReturn(2);

        SellRequest request = new SellRequest("MSFT", 5);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> portfolioService.sellStock(
                                "test@example.com",
                                request
                        )
                );

        assertEquals(
                "Cannot sell more shares than you own",
                exception.getMessage()
        );
    }

    @Test
    void sellStockShouldSuccessfullySellShares() {

        User user = new User(
                "Test User",
                "test@example.com",
                "password",
                "USER",
                1000.0
        );

        user.setId("user-1");

        Asset asset = new Asset(
                "MSFT",
                "Microsoft",
                "Technology",
                500.0
        );

        Portfolio portfolio = new Portfolio(
                "user-1",
                "MSFT",
                5,
                400.0
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(assetRepository.findBySymbol("MSFT"))
                .thenReturn(Optional.of(asset));

        when(portfolioRepository.findByUserIdAndSymbol(
                "user-1",
                "MSFT"
        )).thenReturn(Optional.of(portfolio));

        SellRequest request = new SellRequest("MSFT", 2);

        SellResponse response =
                portfolioService.sellStock(
                        "test@example.com",
                        request
                );

        assertEquals("MSFT", response.getSymbol());
        assertEquals(2, response.getQuantity());
        assertEquals(500.0, response.getPricePerShare());
        assertEquals(1000.0, response.getTotalValue());
        assertEquals(2000.0, response.getRemainingBalance());

        assertEquals(2000.0, user.getBalance());
        assertEquals(3, portfolio.getQuantity());

        verify(userRepository).save(user);
        verify(portfolioRepository).save(portfolio);

        verify(transactionService).recordTransaction(
                "user-1",
                "MSFT",
                "SELL",
                2,
                500.0,
                1000.0
        );
    }
}