package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.ShopRegistrationRequestDto;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
