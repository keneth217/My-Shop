package com.laptop.Laptop.controller;

import com.laptop.Laptop.constants.MyConstants;
import com.laptop.Laptop.dto.Responsedto;
import com.laptop.Laptop.dto.SaleRequest;
import com.laptop.Laptop.entity.Cart;
import com.laptop.Laptop.entity.Sale;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.helper.AuthUser;
import com.laptop.Laptop.services.SalesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {
    @Autowired
    private SalesServices salesServices;

    @Autowired
    private AuthUser authUser;

  User loggedInUser = authUser.getLoggedInUserDetails();
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        // Implement this to get the logged-in user
        Cart cart = salesServices.addToCart(productId, quantity, loggedInUser);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Sale> checkout(@RequestParam String customerName, @RequestParam String customerPhone) throws IOException {

        Sale sale = salesServices.checkoutCart(loggedInUser, customerName, customerPhone);
        return ResponseEntity.status(HttpStatus.CREATED).body(sale);
    }

    @GetMapping
    public ResponseEntity<Cart> getCart() {

        Cart cart = salesServices.getCartItems(loggedInUser);
        return ResponseEntity.ok(cart);
    }
}
