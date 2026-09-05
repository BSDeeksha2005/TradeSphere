package com.tradesphere.dto;

public class LoginResponse {

    private String id;
    private String name;
    private String email;
    private String role;
    private double balance;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(
            String id,
            String name,
            String email,
            String role,
            double balance,
            String token) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.balance = balance;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}