package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.ShopUpdateRequestDto;
import com.laptop.Laptop.services.CustomerShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/shop")
public class CustomerShopController {

@Autowired
private CustomerShopService service;
    @GetMapping("/my-shop")
    public ResponseEntity<ShopUpdateRequestDto> getShopDetailsForCurrentUser() {

        ShopUpdateRequestDto shopDetails = service.getShopDetailsForCurrentUser();

        return ResponseEntity.ok(shopDetails);
    }
    @PutMapping("/update")
    public ResponseEntity<ShopUpdateRequestDto> updateShopDetailsForCurrentUser(
            @ModelAttribute ShopUpdateRequestDto request) { // Accept the MultipartFile here

        ShopUpdateRequestDto updatedShop = service.updateShopDetailsForCurrentUser(request); // Pass the file to the service

        return ResponseEntity.ok(updatedShop);
    }
}
