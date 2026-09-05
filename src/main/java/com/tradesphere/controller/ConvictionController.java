package com.tradesphere.controller;

import com.tradesphere.dto.ConvictionRequest;
import com.tradesphere.model.Conviction;
import com.tradesphere.service.ConvictionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/convictions")
public class ConvictionController {

    private final ConvictionService convictionService;

    public ConvictionController(
            ConvictionService convictionService
    ) {
        this.convictionService = convictionService;
    }

    @PostMapping
    public ResponseEntity<?> createConviction(
            @RequestBody ConvictionRequest request,
            Authentication authentication
    ) {

        try {

            Conviction conviction =
                    convictionService.createConviction(
                            authentication.getName(),
                            request
                    );

            return ResponseEntity.ok(conviction);

        } catch (IllegalArgumentException ex) {

            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getConvictions(
            Authentication authentication
    ) {

        try {

            List<Conviction> convictions =
                    convictionService.getUserConvictions(
                            authentication.getName()
                    );

            return ResponseEntity.ok(convictions);

        } catch (IllegalArgumentException ex) {

            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }
}