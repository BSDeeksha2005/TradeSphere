package com.tradesphere.controller;

import com.tradesphere.dto.BuyRequest;
import com.tradesphere.dto.BuyResponse;
import com.tradesphere.service.PortfolioService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.tradesphere.dto.SellRequest;
import com.tradesphere.dto.SellResponse;
import com.tradesphere.model.Portfolio;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyStock(@RequestBody BuyRequest buyRequest) {

        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String email = authentication.getName();

            BuyResponse response =
                    portfolioService.buyStock(email, buyRequest);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());

        } catch (IllegalStateException ex) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }

    @PostMapping("/sell")
public ResponseEntity<?> sellStock(@RequestBody SellRequest sellRequest) {

    try {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        SellResponse response =
                portfolioService.sellStock(email, sellRequest);

        return ResponseEntity.ok(response);

    } catch (IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());

    } catch (IllegalStateException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}
@GetMapping
    public ResponseEntity<?> getPortfolio() {
        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String email = authentication.getName();

            List<Portfolio> portfolio =
                    portfolioService.getUserPortfolio(email);

            return ResponseEntity.ok(portfolio);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }
}