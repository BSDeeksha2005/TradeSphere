package com.tradesphere.controller;

import com.tradesphere.model.Asset;
import com.tradesphere.service.AssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<?> createAsset(@RequestBody Asset asset) {

        try {
            Asset savedAsset = assetService.createAsset(asset);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedAsset);

        } catch (IllegalArgumentException | IllegalStateException ex) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<?> getAssetBySymbol(
            @PathVariable String symbol) {

        try {
            Asset asset = assetService.getAssetBySymbol(symbol);
            return ResponseEntity.ok(asset);

        } catch (IllegalArgumentException ex) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }
}