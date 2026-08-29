# TradeSphere

TradeSphere is a backend trading platform built with Spring Boot, MongoDB and JWT-based authentication.

## Features

- User registration and login
- BCrypt password hashing
- JWT authentication
- Protected REST APIs
- Asset management
- Stock buying and selling
- Portfolio management
- Balance management
- Transaction history
- MongoDB persistence

## Tech Stack

- Java 25
- Spring Boot 4.1.1
- Spring Security
- Spring Data MongoDB
- MongoDB
- JWT
- Maven

## Project Structure

```text
src/main/java/com/tradesphere
│
├── config
│   └── SecurityConfig.java
│
├── controller
│   ├── AssetController.java
│   ├── AuthController.java
│   ├── HealthController.java
│   ├── PortfolioController.java
│   ├── TestController.java
│   └── TransactionController.java
│
├── dto
│   ├── BuyRequest.java
│   ├── BuyResponse.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   ├── RegisterResponse.java
│   ├── SellRequest.java
│   └── SellResponse.java
│
├── model
│   ├── Asset.java
│   ├── Portfolio.java
│   ├── Transaction.java
│   └── User.java
│
├── repository
│   ├── AssetRepository.java
│   ├── PortfolioRepository.java
│   ├── TransactionRepository.java
│   └── UserRepository.java
│
├── security
│   └── JwtAuthenticationFilter.java
│
└── service
    ├── AssetService.java
    ├── AuthService.java
    ├── JwtService.java
    ├── PortfolioService.java
    └── TransactionService.java