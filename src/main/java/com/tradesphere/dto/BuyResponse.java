package com.tradesphere.dto;

public class BuyResponse {

    private String symbol;
    private int quantity;
    private double pricePerShare;
    private double totalCost;
    private double remainingBalance;

    public BuyResponse() {
    }

    public BuyResponse(
            String symbol,
            int quantity,
            double pricePerShare,
            double totalCost,
            double remainingBalance) {

        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.totalCost = totalCost;
        this.remainingBalance = remainingBalance;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPricePerShare() {
        return pricePerShare;
    }

    public void setPricePerShare(double pricePerShare) {
        this.pricePerShare = pricePerShare;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
}