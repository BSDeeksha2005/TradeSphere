package com.tradesphere.controller;

import com.tradesphere.model.DecisionEvent;
import com.tradesphere.service.DecisionEventService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/decisions")
public class DecisionEventController {

    private final DecisionEventService decisionEventService;

    public DecisionEventController(
            DecisionEventService decisionEventService
    ) {
        this.decisionEventService = decisionEventService;
    }

    @PostMapping
    public ResponseEntity<?> createDecision(
            @RequestBody Map<String, Object> request,
            Authentication authentication
    ) {
        try {

            String userId = authentication.getName();

            String symbol = (String) request.get("symbol");
            String direction = (String) request.get("direction");

            Object confidenceValue = request.get("confidence");

            if (confidenceValue == null) {
                throw new IllegalArgumentException(
                        "Confidence is required"
                );
            }

            double confidence =
                    ((Number) confidenceValue).doubleValue();

            String thesis = (String) request.get("thesis");
            String timeHorizon =
                    (String) request.get("timeHorizon");

            DecisionEvent decision =
                    decisionEventService.createDecision(
                            userId,
                            symbol,
                            direction,
                            confidence,
                            thesis,
                            timeHorizon
                    );

            return ResponseEntity.ok(decision);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                    Map.of("message", "Unable to create decision")
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<DecisionEvent>> getDecisions(
            Authentication authentication
    ) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                decisionEventService.getUserDecisions(userId)
        );
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<List<DecisionEvent>> getDecisionsForSymbol(
            @PathVariable String symbol,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                decisionEventService.getUserDecisionsForSymbol(
                        userId,
                        symbol
                )
        );
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveDecision(
            @PathVariable String id,
            Authentication authentication
    ) {

        try {

            String userId = authentication.getName();

            DecisionEvent decision =
                    decisionEventService.resolveDecision(
                            userId,
                            id
                    );

            return ResponseEntity.ok(decision);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );

        } catch (IllegalStateException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }
}
