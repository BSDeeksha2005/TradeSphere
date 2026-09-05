package com.tradesphere.controller;

import com.tradesphere.dto.LoginRequest;
import com.tradesphere.dto.LoginResponse;
import com.tradesphere.dto.RegisterRequest;
import com.tradesphere.dto.RegisterResponse;
import com.tradesphere.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest) {

        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage());
        }
    }
}