package com.tradesphere.service;

import com.tradesphere.dto.LoginRequest;
import com.tradesphere.dto.LoginResponse;
import com.tradesphere.dto.RegisterRequest;
import com.tradesphere.dto.RegisterResponse;
import com.tradesphere.model.User;
import com.tradesphere.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {

        Optional<User> existingUser =
                userRepository.findByEmail(registerRequest.getEmail());

        if (existingUser.isPresent()) {
            throw new IllegalStateException("Email already registered");
        }

        User newUser = new User();

        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(
                passwordEncoder.encode(registerRequest.getPassword())
        );
        newUser.setRole("USER");
        newUser.setBalance(100000.0);

        User savedUser = userRepository.save(newUser);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getBalance()
        );
    }

    public LoginResponse login(LoginRequest loginRequest) {

        Optional<User> existingUser =
                userRepository.findByEmail(loginRequest.getEmail());

        if (existingUser.isEmpty()) {
            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        User user = existingUser.get();

        boolean passwordMatches = passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                token
        );
    }
}