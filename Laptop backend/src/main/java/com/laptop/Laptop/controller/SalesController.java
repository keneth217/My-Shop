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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {
    @Autowired
    private SalesServices salesServices;

    // Utility method to get the logged-in user's details from the authentication token
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Ensure User implements UserDetails
    }
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        User loggedInUser = getLoggedInUser();
        // Implement this to get the logged-in user
        Cart cart = salesServices.addToCart(productId, quantity, loggedInUser);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Sale> checkout(@RequestParam String customerName, @RequestParam String customerPhone) throws IOException {
        User loggedInUser = getLoggedInUser();
        Sale sale = salesServices.checkoutCart(loggedInUser, customerName, customerPhone);
        return ResponseEntity.status(HttpStatus.CREATED).body(sale);
    }

    @GetMapping
    public ResponseEntity<Cart> getCart() {
        User loggedInUser = getLoggedInUser();
        Cart cart = salesServices.getCartItems(loggedInUser);
        return ResponseEntity.ok(cart);
    }
}
