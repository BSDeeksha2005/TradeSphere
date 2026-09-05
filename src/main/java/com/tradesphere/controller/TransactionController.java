package com.tradesphere.controller;

import com.tradesphere.model.Transaction;
import com.tradesphere.model.User;
import com.tradesphere.repository.UserRepository;
import com.tradesphere.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(
            TransactionService transactionService,
            UserRepository userRepository) {

        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getTransactions() {

        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found"));

            List<Transaction> transactions =
                    transactionService.getUserTransactions(user.getId());

            return ResponseEntity.ok(transactions);

        } catch (IllegalArgumentException ex) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }
}