package com.tradesphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "convictions")
public class Conviction {

    @Id
    private String id;

    private String userId;
    private String symbol;
    private String thesis;
    private int conviction;
    private double entryPrice;
    private int quantity;
    private String tradeType;
    private LocalDateTime createdAt;

    public Conviction() {
    }

    public Conviction(
            String userId,
            String symbol,
            String thesis,
            int conviction,
            double entryPrice,
            int quantity,
            String tradeType
    ) {
        this.userId = userId;
        this.symbol = symbol;
        this.thesis = thesis;
        this.conviction = conviction;
        this.entryPrice = entryPrice;
        this.quantity = quantity;
        this.tradeType = tradeType;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
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

    public String getThesis() {
        return thesis;
    }

    public void setThesis(String thesis) {
        this.thesis = thesis;
    }

    public int getConviction() {
        return conviction;
    }

    public void setConviction(int conviction) {
        this.conviction = conviction;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}