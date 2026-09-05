package com.tradesphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "decision_events")
public class DecisionEvent {

    @Id
    private String id;

    private String userId;
    private String symbol;

    private String direction;
    private double confidence;

    private String thesis;
    private String timeHorizon;

    private double priceAtDecision;

    private String outcome;
    private Double priceAtResolution;
    private Double returnPercent;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public DecisionEvent() {
    }

    public DecisionEvent(
            String userId,
            String symbol,
            String direction,
            double confidence,
            String thesis,
            String timeHorizon,
            double priceAtDecision
    ) {
        this.userId = userId;
        this.symbol = symbol;
        this.direction = direction;
        this.confidence = confidence;
        this.thesis = thesis;
        this.timeHorizon = timeHorizon;
        this.priceAtDecision = priceAtDecision;

        this.outcome = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getThesis() {
        return thesis;
    }

    public void setThesis(String thesis) {
        this.thesis = thesis;
    }

    public String getTimeHorizon() {
        return timeHorizon;
    }

    public void setTimeHorizon(String timeHorizon) {
        this.timeHorizon = timeHorizon;
    }

    public double getPriceAtDecision() {
        return priceAtDecision;
    }

    public void setPriceAtDecision(double priceAtDecision) {
        this.priceAtDecision = priceAtDecision;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Double getPriceAtResolution() {
        return priceAtResolution;
    }

    public void setPriceAtResolution(Double priceAtResolution) {
        this.priceAtResolution = priceAtResolution;
    }

    public Double getReturnPercent() {
        return returnPercent;
    }

    public void setReturnPercent(Double returnPercent) {
        this.returnPercent = returnPercent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
