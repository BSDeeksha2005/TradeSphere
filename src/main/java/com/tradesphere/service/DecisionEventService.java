package com.tradesphere.service;

import com.tradesphere.model.Asset;
import com.tradesphere.model.DecisionEvent;
import com.tradesphere.repository.AssetRepository;
import com.tradesphere.repository.DecisionEventRepository;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DecisionEventService {

    private final DecisionEventRepository decisionEventRepository;
    private final AssetRepository assetRepository;

    public DecisionEventService(
            DecisionEventRepository decisionEventRepository,
            AssetRepository assetRepository
    ) {
        this.decisionEventRepository = decisionEventRepository;
        this.assetRepository = assetRepository;
    }

    public DecisionEvent createDecision(
            String userId,
            String symbol,
            String direction,
            double confidence,
            String thesis,
            String timeHorizon
    ) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol is required");
        }

        if (direction == null || direction.isBlank()) {
            throw new IllegalArgumentException("Direction is required");
        }

        if (!direction.equals("BULLISH")
                && !direction.equals("NEUTRAL")
                && !direction.equals("BEARISH")) {
            throw new IllegalArgumentException(
                    "Direction must be BULLISH, NEUTRAL, or BEARISH"
            );
        }

        if (confidence < 0 || confidence > 100) {
            throw new IllegalArgumentException(
                    "Confidence must be between 0 and 100"
            );
        }

        if (thesis == null || thesis.isBlank()) {
            throw new IllegalArgumentException("Thesis is required");
        }

        if (timeHorizon == null || timeHorizon.isBlank()) {
            throw new IllegalArgumentException("Time horizon is required");
        }

        Asset asset = assetRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Asset not found: " + symbol
                        )
                );

        DecisionEvent decision = new DecisionEvent(
                userId,
                asset.getSymbol(),
                direction.toUpperCase(),
                confidence,
                thesis,
                timeHorizon,
                asset.getPrice()
        );

        return decisionEventRepository.save(decision);
    }

    public List<DecisionEvent> getUserDecisions(String userId) {

        List<DecisionEvent> decisions =
                decisionEventRepository
                        .findByUserIdOrderByCreatedAtDesc(userId);

        for (DecisionEvent decision : decisions) {
            if ("PENDING".equals(decision.getOutcome())
                    && isReadyForResolution(decision)) {

                resolveDecision(decision);
            }
        }

        return decisions;
    }

    public List<DecisionEvent> getUserDecisionsForSymbol(
            String userId,
            String symbol
    ) {
        List<DecisionEvent> decisions =
                decisionEventRepository
                        .findByUserIdAndSymbolOrderByCreatedAtDesc(
                                userId,
                                symbol.toUpperCase()
                        );

        for (DecisionEvent decision : decisions) {
            if ("PENDING".equals(decision.getOutcome())
                    && isReadyForResolution(decision)) {

                resolveDecision(decision);
            }
        }

        return decisions;
    }

    public DecisionEvent resolveDecision(
            String userId,
            String decisionId
    ) {
        DecisionEvent decision =
                decisionEventRepository.findById(decisionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Decision not found"
                                )
                        );

        if (!decision.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "You cannot resolve another user's decision"
            );
        }

        if (!"PENDING".equals(decision.getOutcome())) {
            return decision;
        }

        if (!isReadyForResolution(decision)) {
            throw new IllegalStateException(
                    "This decision is not ready to be resolved yet"
            );
        }

        resolveDecision(decision);

        return decision;
    }

    private void resolveDecision(DecisionEvent decision) {

        Asset asset = assetRepository
                .findBySymbol(decision.getSymbol())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Asset not found: "
                                        + decision.getSymbol()
                        )
                );

        double currentPrice = asset.getPrice();

        double returnPercent =
                ((currentPrice - decision.getPriceAtDecision())
                        / decision.getPriceAtDecision()) * 100.0;

        boolean correct;

        switch (decision.getDirection()) {

            case "BULLISH":
                correct = returnPercent > 0;
                break;

            case "BEARISH":
                correct = returnPercent < 0;
                break;

            case "NEUTRAL":
                correct = Math.abs(returnPercent) <= 2.0;
                break;

            default:
                throw new IllegalStateException(
                        "Unknown decision direction: "
                                + decision.getDirection()
                );
        }

        decision.setPriceAtResolution(currentPrice);
        decision.setReturnPercent(returnPercent);
        decision.setOutcome(correct ? "CORRECT" : "INCORRECT");
        decision.setResolvedAt(LocalDateTime.now());

        decisionEventRepository.save(decision);
    }

    private boolean isReadyForResolution(
            DecisionEvent decision
    ) {

        if (decision.getCreatedAt() == null) {
            return false;
        }

        long requiredHours =
                getHorizonHours(decision.getTimeHorizon());

        long elapsedHours =
                Duration.between(
                        decision.getCreatedAt(),
                        LocalDateTime.now()
                ).toHours();

        return elapsedHours >= requiredHours;
    }

    private long getHorizonHours(String horizon) {

        String normalized =
                horizon.trim().toUpperCase();

        if (normalized.contains("DAY")) {
            return 24;
        }

        if (normalized.contains("WEEK")) {
            return 24 * 7;
        }

        if (normalized.contains("MONTH")) {
            return 24 * 30;
        }

        if (normalized.contains("YEAR")) {
            return 24 * 365;
        }

        /*
         * Useful for demo/testing if the UI ever uses
         * a short custom horizon.
         */
        if (normalized.contains("HOUR")) {
            return 1;
        }

        return 24 * 30;
    }
}
