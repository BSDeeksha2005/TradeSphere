package com.tradesphere.dto;

public class SellResponse {

    private String symbol;
    private int quantity;
    private double pricePerShare;
    private double totalValue;
    private double remainingBalance;

    public SellResponse() {
    }

    public SellResponse(
            String symbol,
            int quantity,
            double pricePerShare,
            double totalValue,
            double remainingBalance) {

        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.totalValue = totalValue;
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

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
}