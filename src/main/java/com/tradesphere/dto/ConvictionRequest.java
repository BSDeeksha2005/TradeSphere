package com.tradesphere.dto;

public class ConvictionRequest {

    private String symbol;
    private String thesis;
    private int conviction;
    private double entryPrice;
    private int quantity;
    private String tradeType;

    public ConvictionRequest() {
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
}