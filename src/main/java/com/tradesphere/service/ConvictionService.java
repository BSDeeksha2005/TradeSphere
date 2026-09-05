package com.tradesphere.service;

import com.tradesphere.dto.ConvictionRequest;
import com.tradesphere.model.Conviction;
import com.tradesphere.model.User;
import com.tradesphere.repository.ConvictionRepository;
import com.tradesphere.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConvictionService {

    private final ConvictionRepository convictionRepository;
    private final UserRepository userRepository;

    public ConvictionService(
            ConvictionRepository convictionRepository,
            UserRepository userRepository
    ) {
        this.convictionRepository = convictionRepository;
        this.userRepository = userRepository;
    }

    public Conviction createConviction(
            String email,
            ConvictionRequest request
    ) {

        if (request.getThesis() == null ||
                request.getThesis().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Investment thesis is required"
            );
        }

        if (request.getConviction() < 1 ||
                request.getConviction() > 5) {

            throw new IllegalArgumentException(
                    "Conviction must be between 1 and 5"
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Conviction conviction = new Conviction(
                user.getId(),
                request.getSymbol(),
                request.getThesis().trim(),
                request.getConviction(),
                request.getEntryPrice(),
                request.getQuantity(),
                request.getTradeType()
        );

        return convictionRepository.save(conviction);
    }

    public List<Conviction> getUserConvictions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return convictionRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());
    }
}