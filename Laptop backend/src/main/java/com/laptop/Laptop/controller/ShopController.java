package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.ShopRegistrationRequestDto;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.enums.ShopStatus;
import com.laptop.Laptop.services.ShopService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @PostMapping("/register")
    public ResponseEntity<Shop> registerShop(@RequestBody ShopRegistrationRequestDto request) {
        Shop newShop = shopService.registerShop(request);
        return new ResponseEntity<>(newShop, HttpStatus.CREATED);
    }

    // Activate a shop
    // Activate a shop
    @PostMapping("/{shopId}/activate")
    public ResponseEntity<Shop> activateShop(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopRegistrationRequestDto request) {
        Shop activatedShop = shopService.activateShop(shopId, request);
        return ResponseEntity.ok(activatedShop);
    }

    // Deactivate a shop
    @PutMapping("/{shopId}/deactivate")
    public ResponseEntity<Shop> deactivateShop(@PathVariable Long shopId) {
        Shop deactivatedShop = shopService.deactiveShop(shopId);
        return ResponseEntity.ok(deactivatedShop);
    }


    // Get all registered shops
    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {
        List<Shop> shops = shopService.getAllShops();
        return ResponseEntity.ok(shops);
    }

    // Get all shops by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Shop>> getAllShopsByStatus(@PathVariable ShopStatus status) {
        List<Shop> shops = shopService.getAllShopsByStatus(status);
        return ResponseEntity.ok(shops);
    }
}
